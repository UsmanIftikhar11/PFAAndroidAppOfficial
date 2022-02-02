package com.pfa.pfaapp.customviews;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pfa.pfaapp.R;

public class PullAndLoadListView extends ListView implements AbsListView.OnScrollListener {

    /**
     * Load more types <br/>
     * Automatic Load more sliding in the end portion
     */
    public static final int GET_MORE_TYPE_AUTO = 0;

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        doOnScroll(firstVisibleItem, visibleItemCount, totalItemCount);
    }

    /**
     * Drop-down refresh the interface
     */
    public interface OnRefreshListener {
        void onRefresh();
    }

    /**
     * Load more Interface
     */
    public interface OnGetMoreListener {
        void onGetMore(int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    // Completion status ( initial state )
    private final static int NONE = 0;

    // Drop-down refresh state
    private final static int PULL_TO_REFRESH = 1;

    // Loosen Refresh Status
    private final static int RELEASE_TO_REFRESH = 2;

    // Refreshing state
    private final static int REFRESHING = 3;

    //In fact, the offset distance on the distance from the interface padding ratio ( the proportion of delay , the more difficult drag )
    private final static float RATIO = 1.7f;

    // Refreshing the View drop-down ( head view )
    private ViewGroup headView;

    // Refresh drop-down text
    private TextView tvHeadTitleTV;

    // Drop-down refresh icon
//    private GifImageView ivHeadArrow;

    //	Head height
    private int headViewHeight;
    // status
    private int state = NONE;

    /**
     * Mark the initial position has a record slide in a record time .
     * In order to better deal with the slide , according to need , not record the initial position <br/> ACTION_DOWN
     * Instead ACTION_MOVE the first qualifying record in the initial position of the trigger
     */
    private boolean isStartRecorded = false;

    // For recording slide began when the value of Y
    private float startY;

    /**
     * Custom attributes , whether to add the user to control their own drop-down refresh Header <br/>
     * <p>
     * The default is false, the drop-down refresh Header will be added in the constructor to PullListView , as the first Header , is displayed in the uppermost <br/>
     * If a user adds an extra Header, additional Header in the drop-down refresh under Header. <br/>
     * Users sometimes need to control the sequential addition Header , you can set this property to true, and at the right time , the initiative to call addPullHeader () method to add drop-down refresh Header
     */
    private boolean addPullHeaderByUser = false;
    /**
     * Custom attributes <br/>
     * Load more trigger <br/>
     * The default is sliding in the end portion is automatically loaded more
     */
    private int getMoreType = GET_MORE_TYPE_AUTO;

    /**
     * Analyzing the drop-down refresh state is a change from the initial state , or by the state change from release to refresh <br/>
     * True is represented by a state transition from the refresh release
     */
    private boolean isFromReleaseToRefresh;

    // Has added load more footer logo
    private boolean addGetMoreFooterFlag = false;

    //	If there are more data flag
    private boolean hasMoreDataFlag = true;

    /**
     * Finally, a number of Item arrives Scroll, only the first can trigger automatic refresh
     */
    private int reachLastPositionCount = 0;

    private OnRefreshListener refreshListener;
    private OnGetMoreListener getMoreListener;

    private boolean canRefresh;
    private boolean isGetMoreing = false;

    public PullAndLoadListView(Context context) {
        this(context, null, 0);
    }

    public PullAndLoadListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullAndLoadListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * initialization
     */

    @SuppressLint("InflateParams")
    private void init(Context context, AttributeSet attrs) {
        //Get Property
        @SuppressLint("CustomViewStyleable") TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PullListView, 0, 0);
        if (arr != null) {
            addPullHeaderByUser = arr.getBoolean(R.styleable.PullListView_addPullHeaderByUser, addPullHeaderByUser);
            getMoreType = arr.getInt(R.styleable.PullListView_getMoreType, GET_MORE_TYPE_AUTO);
            arr.recycle();
        }
        initAnimation();
        LayoutInflater inflater = LayoutInflater.from(context);

        /*
         * head
         */
        headView = (RelativeLayout) inflater.inflate(R.layout.pull_list_view_head, null);
        tvHeadTitleTV = headView.findViewById(R.id.tvHeadTitleTV);

        if (headView != null) {
            measureView(headView);
        }
        assert headView != null;
        headViewHeight = headView.getMeasuredHeight();
        headView.setPadding(0, -headViewHeight, 0, 0);
        headView.invalidate();

        if (!addPullHeaderByUser) {
            addHeaderView(headView, null, false);
        }

        setOnScrollListener(this);
        state = NONE;
        canRefresh = false;
    }

    /**
     * Initialization Animation
     */
    private void initAnimation() {
        //Reverse rotation
        // Reverse rotation animation
        RotateAnimation reverseAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(300);
        reverseAnimation.setFillAfter(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Can not drop down to refresh
        if (!canRefresh) {
            return super.dispatchTouchEvent(event);
        }

        int action = event.getAction();
        float tempY = event.getRawY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (state == PULL_TO_REFRESH) {
                    state = NONE;
                    changeHeaderViewByState();
                    refresh();
                } else if (state == RELEASE_TO_REFRESH) {
                    state = REFRESHING;
                    changeHeaderViewByState();
                    refresh();
                }
                isStartRecorded = false;
                isFromReleaseToRefresh = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (!checkCanPullDown() || state == REFRESHING) {
                    break;
                }
                if (!isStartRecorded) {
                    startY = tempY;
//                    Log.v(TAG, "Record the initial position at the start of the slide");
                    isStartRecorded = true;
                }
                float deltaY = tempY - startY-200;
                float realDeltaY = deltaY / RATIO;

                // The initial state
                if (state == NONE) {
                    if (realDeltaY > 0) {
                        state = PULL_TO_REFRESH;
                        changeHeaderViewByState();
//                        Log.v(TAG, "From the initial state to the pull-down refresh state");
                    }
                }
                // Has not yet arrived when the display refresh release state PULL_TO_REFRESH
                else if (state == PULL_TO_REFRESH) {

                    headView.setPadding(0, -headViewHeight + (int) realDeltaY, 0, 0);
                    // Scroll down to enter the state RELEASE_TO_REFRESH
                    if (realDeltaY >= headViewHeight) {
                        state = RELEASE_TO_REFRESH;
                        isFromReleaseToRefresh = true;
                        changeHeaderViewByState();
//                        Log.v(TAG, "Drop-down refresh refresh state transition to loosen");
                    }
                    // Push the top
                    else if (realDeltaY <= 0) {
                        state = NONE;
                        changeHeaderViewByState();
//                        Log.v(TAG, "Drop-down refresh transition to the initial state");
                    }
                }
                // You can let go to refresh
                else if (state == RELEASE_TO_REFRESH) {

                    headView.setPadding(0, -headViewHeight + (int) realDeltaY, 0, 0);
                    // Push up , and pushed to the screen enough to cover up the extent of the head , but have not pushed to the point where the entire cover
                    if (realDeltaY < headViewHeight && realDeltaY > 0) {
                        state = PULL_TO_REFRESH;
                        changeHeaderViewByState();
//                        Log.v(TAG, "Release by the state to refresh the drop-down refresh state");
                    }
                    // Suddenly pushed to the top of the
                    else if (realDeltaY <= 0) {
                        state = NONE;
                        changeHeaderViewByState();
//                        Log.v(TAG, "Loosen the refresh state to the initial state");
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    // When the state change when the method is called to update the interface
    private void changeHeaderViewByState() {
        switch (state) {
            case NONE:
                headView.setPadding(0, -1 * headViewHeight, 0, 0);
                tvHeadTitleTV.setText(getResources().getString(R.string.pull_down_to_refresh)); //"Pull down to refresh"
                break;

            case PULL_TO_REFRESH:
                tvHeadTitleTV.setVisibility(View.VISIBLE);
                tvHeadTitleTV.setText(getResources().getString(R.string.pull_down_to_refresh));
                // State transition is made to RELEASE_To_REFRESH
                if (isFromReleaseToRefresh) {
                    isFromReleaseToRefresh = false;
                }
                break;

            case RELEASE_TO_REFRESH:
                tvHeadTitleTV.setVisibility(View.VISIBLE);
                tvHeadTitleTV.setText(getResources().getString(R.string.refresh_release));
                break;

            case REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                tvHeadTitleTV.setText(getResources().getString(R.string.refreshing));
                break;
        }
    }

    // Refresh
    private void refresh() {
        //Refresh callback
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
//        //Load more recharge
        isGetMoreing = false;
        hasMoreDataFlag = true;
    }

    // load more
    private void getMore(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //Load more callbacks
        if (getMoreListener != null) {
            isGetMoreing = true;
            getMoreListener.onGetMore(firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    // Measurement view
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * Determine whether the drop-down <br/>
     * ListView is judged whether the slide to the top
     */
    private boolean checkCanPullDown() {
        return getFirstVisiblePosition() <= 1 && canScroll(-1);
    }

    /**
     * Determine whether to automatically load more <br/>
     */
    private boolean checkCanAutoGetMore() {

        return getMoreType == GET_MORE_TYPE_AUTO && getMoreListener != null && !isGetMoreing && hasMoreDataFlag &&
                getAdapter() != null && !(canScroll(1) && canScroll(-1)) && (reachLastPositionCount >= 1 && reachLastPositionCount <= 10);

    }

    /**
     * Determine whether the slide ListView
     */
    private boolean canScroll(int direction) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }

        final int firstPosition = getFirstVisiblePosition();
        final int listPaddingTop = getPaddingTop();
        final int listPaddingBottom = getPaddingTop();
        final int itemCount = getAdapter().getCount();

        if (direction > 0) {
            final int lastBottom = getChildAt(childCount - 1).getBottom();
            final int lastPosition = firstPosition + childCount;
            return lastPosition >= itemCount && lastBottom <= getHeight() - listPaddingBottom;
        } else {
            final int firstTop = getChildAt(0).getTop();
            return firstPosition <= 0 && firstTop >= listPaddingTop;
        }
    }

    /**
     * Settings pull-down refresh listeners
     */
    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        canRefresh = true;
    }

    /**
     * Setting Load more listeners
     */
    public void setOnGetMoreListener(OnGetMoreListener getMoreListener) {
        this.getMoreListener = getMoreListener;
        if (!addGetMoreFooterFlag) {
            addGetMoreFooterFlag = true;
        }
    }

    /**
     * Drop-down refresh completion
     */
    public void refreshComplete() {
        state = NONE;
        changeHeaderViewByState();
    }

    /**
     * Load more complete
     */
    public void getMoreComplete() {
        isGetMoreing = false;
    }

    /**
     * If the project elsewhere in the need to re- set PullListView of OnScrollListener <br/>
     * Please call this method within onScroll method in the new listener , ensuring Pull List View running.
     */
    public void doOnScroll(int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if (getAdapter() == null) {
            return;
        }

        if (getLastVisiblePosition() == getAdapter().getCount() - 1) {
            reachLastPositionCount++;
        } else {
            reachLastPositionCount = 0;
        }

        int remainingCount = totalItemCount - (firstVisibleItem + visibleItemCount);
        if (remainingCount < 30) {

            getMore(firstVisibleItem, visibleItemCount, totalItemCount);

            return;
        }
        if (checkCanAutoGetMore()) {
            getMore(firstVisibleItem, visibleItemCount, totalItemCount);
        }

    }
}

