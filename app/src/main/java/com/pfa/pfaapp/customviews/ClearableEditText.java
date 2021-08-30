package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;

import com.pfa.pfaapp.interfaces.TextWatcherAdapter;
import com.pfa.pfaapp.utils.AppUtils;


/**
 * To clear icon can be changed via
 * <pre>
 * android:drawable(Right|Left)="@drawable/custom_icon"
 * </pre>
 */
public class ClearableEditText extends AppCompatEditText implements OnTouchListener, OnFocusChangeListener, TextWatcherAdapter.TextWatcherListener {



    public enum Location {
		LEFT(0), RIGHT(2);

		final int idx;

		Location(int idx) {
			this.idx = idx;
		}
	}

	public interface Listener {
		void didClearText();
	}

	public ClearableEditText(Context context) {
		super(context);
		init();
	}

	public ClearableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		this.l = l;
	}

	@Override
	public void setOnFocusChangeListener(OnFocusChangeListener f) {
		this.f = f;
	}

	private Location loc = Location.RIGHT;

	private Drawable xD;
	private Listener listener;

	private OnTouchListener l;
	private OnFocusChangeListener f;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (getDisplayedDrawable() != null) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			int left = (loc == Location.LEFT) ? 0 : getWidth() - getPaddingRight() - xD.getIntrinsicWidth();
			int right = (loc == Location.LEFT) ? getPaddingLeft() + xD.getIntrinsicWidth() : getWidth();
			boolean tappedX = x >= left && x <= right && y >= 0 && y <= (getBottom() - getTop());
			if (tappedX) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					setText("");
					if (listener != null) {
						listener.didClearText();
					}
				}
				return true;
			}
		}
		return l != null && l.onTouch(v, event);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			setClearIconVisible(isNotEmpty(getText().toString()));
		} else {
			setClearIconVisible(false);
		}
		if (f != null) {
			f.onFocusChange(v, hasFocus);
		}
	}

	private boolean isNotEmpty(String text) {
		return !text.isEmpty();
	}

	@Override
	public void onTextChanged(AppCompatEditText view, String text) {
		if (isFocused()) {
			setClearIconVisible(isNotEmpty(text));
		}
	}

	@Override
	public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
		super.setCompoundDrawables(left, top, right, bottom);
		initIcon();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void init() {
		super.setOnTouchListener(this);
		super.setOnFocusChangeListener(this);

		AppUtils appUtils= new AppUtils(getContext());
		if(!appUtils.isEnglishLang())
			loc = Location.LEFT;

		addTextChangedListener(new TextWatcherAdapter(this, this));
		initIcon();
		setClearIconVisible(false);

	}

	private void initIcon() {
		xD = null;
		if (loc != null) {
			xD = getCompoundDrawables()[loc.idx];
		}
		if (xD == null) {
			xD = getResources().getDrawable(android.R.drawable.presence_offline);
		}
		xD.setBounds(0, 0, xD.getIntrinsicWidth(), xD.getIntrinsicHeight());
		int min = getPaddingTop() + xD.getIntrinsicHeight() + getPaddingBottom();
		if (getSuggestedMinimumHeight() < min) {
			setMinimumHeight(min);
		}
	}

	private Drawable getDisplayedDrawable() {
		return (loc != null) ? getCompoundDrawables()[loc.idx] : null;
	}

	protected void setClearIconVisible(boolean visible) {
		Drawable[] cd = getCompoundDrawables();
		Drawable displayed = getDisplayedDrawable();
		boolean wasVisible = (displayed != null);
		if (visible != wasVisible) {
			Drawable x = visible ? xD : null;
			super.setCompoundDrawables((loc == Location.LEFT) ? x : cd[0], cd[1], (loc == Location.RIGHT) ? x : cd[2],
					cd[3]);
		}
	}
}
