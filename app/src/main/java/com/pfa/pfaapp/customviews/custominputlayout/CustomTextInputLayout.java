package com.pfa.pfaapp.customviews.custominputlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.annotation.VisibleForTesting;
import com.google.android.material.internal.CheckableImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.customview.view.AbsSavedState;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.TextViewCompat;
import androidx.coordinatorlayout.widget.ViewGroupUtils;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TintTypedArray;
import androidx.appcompat.widget.WithHint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

/**
 * Layout which wraps an {@link android.widget.EditText} (or descendant) to show a floating label
 * when the hint is hidden due to the user inputting text.
 * <p>
 * <p>Also supports showing an error via {@link #setErrorEnabled(boolean)} and
 * {@link #setError(CharSequence)}, and a character counter via
 * {@link #setCounterEnabled(boolean)}.</p>
 * <p>
 * <p>Password visibility toggling is also supported via the
 * API and related attribute.
 * If enabled, a button is displayed to toggle between the password being displayed as plain-text
 * or disguised, when your EditText is set to display a password.</p>
 * <p>
 * <p><strong>Note:</strong> When using the password toggle functionality, the 'end' compound
 * drawable of the EditText will be overridden while the toggle is enabled. To ensure that any
 * existing drawables are restored correctly, you should set those compound drawables relatively
 * (start/end), opposed to absolutely (left/right).</p>
 * <p>
 * The {@link CustomTextInputLayout} class is provided to be used as a child of this layout. Using
 * TextInputEditText allows TextInputLayout greater control over the visual aspects of any
 * text input. An example usage is as so:
 * <p>
 * <pre>
 * &lt;CustomTextInputLayout
 *         android:layout_width=&quot;match_parent&quot;
 *         android:layout_height=&quot;wrap_content&quot;&gt;
 *
 *     &lt;widget.TextInputEditText
 *             android:layout_width=&quot;match_parent&quot;
 *             android:layout_height=&quot;wrap_content&quot;
 *             android:hint=&quot;@string/form_username&quot;/&gt;
 *
 * &lt;/CustomTextInputLayout&gt;
 * </pre>
 * <p>
 * <p><strong>Note:</strong> The actual view hierarchy present under CustomTextInputLayout is
 * <strong>NOT</strong> guaranteed to match the view hierarchy as written in XML. As a result,
 * calls to getParent() on children of the CustomTextInputLayout -- such as an TextInputEditText --
 * may not return the CustomTextInputLayout itself, but rather an intermediate View. If you need
 * to access a View directly, set an {@code android:id} and use {@link View#findViewById(int)}.
 */
@SuppressLint("RestrictedApi")
public class CustomTextInputLayout extends LinearLayout implements WithHint {

    private static final int ANIMATION_DURATION = 200;
    private static final int INVALID_MAX_LENGTH = -1;

    private final FrameLayout mInputFrame;
    EditText mEditText;
    private CharSequence mOriginalHint;

    private boolean mHintEnabled;
    private CharSequence mHint;

    private Paint mTmpPaint;
    private final Rect mTmpRect = new Rect();

    private LinearLayout mIndicatorArea;
    private int mIndicatorsAdded;

    private Typeface mTypeface;

    private boolean mErrorEnabled;
    TextView mErrorView;
    private int mErrorTextAppearance;
    private boolean mErrorShown;
    private CharSequence mError;

    boolean mCounterEnabled;
    private TextView mCounterView;
    private int mCounterMaxLength;
    private int mCounterTextAppearance;
    private int mCounterOverflowTextAppearance;
    private boolean mCounterOverflowed;

    private boolean mPasswordToggleEnabled;
    private Drawable mPasswordToggleDrawable;
    private CharSequence mPasswordToggleContentDesc;
    private CheckableImageButton mPasswordToggleView;
    private boolean mPasswordToggledVisible;
    private Drawable mPasswordToggleDummyDrawable;
    private Drawable mOriginalEditTextEndDrawable;

    private ColorStateList mPasswordToggleTintList;
    private boolean mHasPasswordToggleTintList;
    private PorterDuff.Mode mPasswordToggleTintMode;
    private boolean mHasPasswordToggleTintMode;

    private ColorStateList mDefaultTextColor;
    private ColorStateList mFocusedTextColor;

    // Only used for testing
    private boolean mHintExpanded;

    final CustomCollapsingTextHelper mCollapsingTextHelper = new CustomCollapsingTextHelper(this);

    private boolean mHintAnimationEnabled;
    private ValueAnimator mAnimator;

    private boolean mHasReconstructedEditTextBackground;
    private boolean mInDrawableStateChanged;

    private boolean mRestoringSavedState;

    SharedPrefUtils sharedPrefUtils;

    public CustomTextInputLayout(Context context) {
        this(context, null);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * CustomTextInputLayout constructor
     * */
    @SuppressLint({"RestrictedApi", "PrivateResource"})
    public CustomTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        // Can't call through to super(Context, AttributeSet, int) since it doesn't exist on API 10
        super(context, attrs);

        CustomThemeUtils.checkAppCompatTheme(context);
        sharedPrefUtils= new SharedPrefUtils(context);

        setOrientation(VERTICAL);
        setWillNotDraw(false);
        setAddStatesFromChildren(true);

        mInputFrame = new FrameLayout(context);
        mInputFrame.setAddStatesFromChildren(true);
        addView(mInputFrame);

        mCollapsingTextHelper.setTextSizeInterpolator(CustomAnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        mCollapsingTextHelper.setPositionInterpolator(new AccelerateInterpolator());
        mCollapsingTextHelper.setCollapsedTextGravity(Gravity.TOP | GravityCompat.START);

        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.TextInputLayout, defStyleAttr, R.style.Widget_Design_TextInputLayout);
        mHintEnabled = a.getBoolean(R.styleable.TextInputLayout_hintEnabled, true);
        setHint(a.getText(R.styleable.TextInputLayout_android_hint));
        mHintAnimationEnabled = a.getBoolean(R.styleable.TextInputLayout_hintAnimationEnabled, true);

        if (a.hasValue(R.styleable.TextInputLayout_android_textColorHint)) {
            mDefaultTextColor = mFocusedTextColor = a.getColorStateList(R.styleable.TextInputLayout_android_textColorHint);
        }

        final int hintAppearance = a.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, -1);
        if (hintAppearance != -1) {
            setHintTextAppearance(a.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, 0));
        }

        mErrorTextAppearance = a.getResourceId(R.styleable.TextInputLayout_errorTextAppearance, 0);
        final boolean errorEnabled = a.getBoolean(R.styleable.TextInputLayout_errorEnabled, false);

        final boolean counterEnabled = a.getBoolean(R.styleable.TextInputLayout_counterEnabled, false);
        setCounterMaxLength(a.getInt(R.styleable.TextInputLayout_counterMaxLength, INVALID_MAX_LENGTH));
        mCounterTextAppearance = a.getResourceId(R.styleable.TextInputLayout_counterTextAppearance, 0);
        mCounterOverflowTextAppearance = a.getResourceId(R.styleable.TextInputLayout_counterOverflowTextAppearance, 0);

        mPasswordToggleEnabled = a.getBoolean(R.styleable.TextInputLayout_passwordToggleEnabled, false);
        mPasswordToggleDrawable = a.getDrawable(R.styleable.TextInputLayout_passwordToggleDrawable);
        mPasswordToggleContentDesc = a.getText(R.styleable.TextInputLayout_passwordToggleContentDescription);
        if (a.hasValue(R.styleable.TextInputLayout_passwordToggleTint)) {
            mHasPasswordToggleTintList = true;
            mPasswordToggleTintList = a.getColorStateList(R.styleable.TextInputLayout_passwordToggleTint);
        }
        if (a.hasValue(R.styleable.TextInputLayout_passwordToggleTintMode)) {
            mHasPasswordToggleTintMode = true;
            mPasswordToggleTintMode = CustomViewUtils.parseTintMode(a.getInt(R.styleable.TextInputLayout_passwordToggleTintMode, -1));//, null);


        }

        a.recycle();

        setErrorEnabled(errorEnabled);
        setCounterEnabled(counterEnabled);
        applyPasswordToggleTint();

        if (ViewCompat.getImportantForAccessibility(this) == ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            // Make sure we're important for accessibility if we haven't been explicitly not
            ViewCompat.setImportantForAccessibility(this,
                    ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);
        }

        ViewCompat.setAccessibilityDelegate(this, new TextInputAccessibilityDelegate());
    }

    @Override
    public void addView(View child, int index, final ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            // Make sure that the EditText is vertically at the bottom, so that it sits on the
            // EditText's underline
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(params);
            flp.gravity = Gravity.CENTER_VERTICAL | (flp.gravity & ~Gravity.VERTICAL_GRAVITY_MASK);
            mInputFrame.addView(child, flp);

            // Now use the EditText's LayoutParams as our own and update them to make enough space
            // for the label
            mInputFrame.setLayoutParams(params);
            updateInputLayoutMargins();

            setEditText((EditText) child);
        } else {
            // Carry on adding the View...
            super.addView(child, index, params);
        }
    }

    /**
     * Set the typeface to use for the hint and any label views (such as counter and error views).
     *
     * @param typeface typeface to use, or {@code null} to use the default.
     */
    public void setTypeface(@Nullable Typeface typeface) {
        if ((mTypeface != null && !mTypeface.equals(typeface))
                || (mTypeface == null && typeface != null)) {
            mTypeface = typeface;

            mCollapsingTextHelper.setTypefaces(typeface);
            if (mCounterView != null) {
                mCounterView.setTypeface(typeface);
            }
            if (mErrorView != null) {
                mErrorView.setTypeface(typeface);
            }
        }
    }

    /**
     * Returns the typeface used for the hint and any label views (such as counter and error views).
     */
    @NonNull
    public Typeface getTypeface() {
        return mTypeface;
    }

    @Override
    public void dispatchProvideAutofillStructure(ViewStructure structure, int flags) {
        if (mOriginalHint == null || mEditText == null) {
            super.dispatchProvideAutofillStructure(structure, flags);
            return;
        }

        // Temporarily sets child's hint to its original value so it is properly set in the
        // child's ViewStructure.
        final CharSequence hint = mEditText.getHint();
        mEditText.setHint(mOriginalHint);
        try {
            super.dispatchProvideAutofillStructure(structure, flags);
        } finally {
            mEditText.setHint(hint);
        }
    }

    private void setEditText(EditText editText) {
        // If we already have an EditText, throw an exception
        if (mEditText != null) {
            throw new IllegalArgumentException("We already have an EditText, can only have one");
        }

//        if (!(editText instanceof TextInputEditText)) {
//            Log.i(LOG_TAG, "EditText added is not a TextInputEditText. Please switch to using that class instead.");
//        }

        mEditText = editText;

        final boolean hasPasswordTransformation = hasPasswordTransformation();

        // Use the EditText's typeface, and it's text size for our expanded text
        if (!hasPasswordTransformation) {
            // We don't want a monospace font just because we have a password field
            mCollapsingTextHelper.setTypefaces(mEditText.getTypeface());
        }
        mCollapsingTextHelper.setExpandedTextSize(mEditText.getTextSize());

        final int editTextGravity = mEditText.getGravity();
        mCollapsingTextHelper.setCollapsedTextGravity(Gravity.TOP | (editTextGravity & ~Gravity.VERTICAL_GRAVITY_MASK));
        mCollapsingTextHelper.setExpandedTextGravity(editTextGravity);

        // Add a TextWatcher so that we know when the text input has changed
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                updateLabelState(!mRestoringSavedState);
                if (mCounterEnabled) {
                    updateCounter(s.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        // Use the EditText's hint colors if we don't have one set
        if (mDefaultTextColor == null) {
            mDefaultTextColor = mEditText.getHintTextColors();
        }

        // If we do not have a valid hint, try and retrieve it from the EditText, if enabled
        if (mHintEnabled && TextUtils.isEmpty(mHint)) {
            // Save the hint so it can be restored on dispatchProvideAutofillStructure();
            mOriginalHint = mEditText.getHint();
            setHint(mOriginalHint);
            // Clear the EditText's hint as we will display it ourselves
            mEditText.setHint(null);
        }

        if (mCounterView != null) {
            updateCounter(mEditText.getText().length());
        }

        if (mIndicatorArea != null) {
            adjustIndicatorPadding();
        }

        updatePasswordToggleView();

        // Update the label visibility with no animation, but force a state change
        updateLabelState(false, true);
    }

    private void updateInputLayoutMargins() {
        // Create/update the LayoutParams so that we can add enough top margin
        // to the EditText so make room for the label
        final LayoutParams lp = (LayoutParams) mInputFrame.getLayoutParams();
        final int newTopMargin;

        if (mHintEnabled) {
            if (mTmpPaint == null) {
                mTmpPaint = new Paint();
            }
            mTmpPaint.setTypeface(mCollapsingTextHelper.getCollapsedTypeface());
            mTmpPaint.setTextSize(mCollapsingTextHelper.getCollapsedTextSize());
            newTopMargin = (int) -mTmpPaint.ascent();
        } else {
            newTopMargin = 0;
        }

        if (newTopMargin != lp.topMargin) {
            lp.topMargin = newTopMargin;
        }
    }

    void updateLabelState(boolean animate) {
        updateLabelState(animate, false);
    }

    boolean isFocused;

    void updateLabelState(final boolean animate, final boolean force) {
        final boolean isEnabled = isEnabled();
        final boolean hasText = mEditText != null && !TextUtils.isEmpty(mEditText.getText());

        isFocused = arrayContains(getDrawableState(), android.R.attr.state_focused);
        if (hasText)
            isFocused = true;
        final boolean isErrorShowing = !TextUtils.isEmpty(getError());

        if (mDefaultTextColor != null) {
            mCollapsingTextHelper.setExpandedTextColor(mDefaultTextColor);
        }

        if (isEnabled && mCounterOverflowed && mCounterView != null) {
            mCollapsingTextHelper.setCollapsedTextColor(mCounterView.getTextColors());
        } else if (isEnabled && isFocused && mFocusedTextColor != null) {
            mCollapsingTextHelper.setCollapsedTextColor(mFocusedTextColor);
        } else if (mDefaultTextColor != null) {
            mCollapsingTextHelper.setCollapsedTextColor(mDefaultTextColor);
        }

        if (hasText || (isEnabled() && (isFocused || isErrorShowing))) {
            // We should be showing the label so do so if it isn't already
            if (force || mHintExpanded) {
                collapseHint(animate);
            }
        } else {
            // We should not be showing the label so hide it
            if (force || !mHintExpanded) {
                expandHint(animate);
            }
        }
    }

    /**
     * Set the hint to be displayed in the floating label, if enabled.
     *
     * @attr ref R.styleable#TextInputLayout_android_hint
     * @see #setHintEnabled(boolean)
     */
    public void setHint(@Nullable CharSequence hint) {
        if (mHintEnabled) {
            setHintInternal(hint);
            sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
        }
    }

    private void setHintInternal(CharSequence hint) {
        mHint = hint;
        mCollapsingTextHelper.setText(hint);
    }

    /**
     * Returns the hint which is displayed in the floating label, if enabled.
     *
     * @return the hint, or null if there isn't one set, or the hint is not enabled.
     * @attr ref R.styleable#TextInputLayout_android_hint
     */
    @Override
    @Nullable
    public CharSequence getHint() {
        return mHintEnabled ? mHint : null;
    }

    /**
     * Sets whether the floating label functionality is enabled or not in this layout.
     * <p>
     * <p>If enabled, any non-empty hint in the child EditText will be moved into the floating
     * hint, and its existing hint will be cleared. If disabled, then any non-empty floating hint
     * in this layout will be moved into the EditText, and this layout's hint will be cleared.</p>
     *
     * @attr ref R.styleable#TextInputLayout_hintEnabled
     * @see #setHint(CharSequence)
     * @see #isHintEnabled()
     */
    public void setHintEnabled(boolean enabled) {
        if (enabled != mHintEnabled) {
            mHintEnabled = enabled;

            final CharSequence editTextHint = mEditText.getHint();
            if (!mHintEnabled) {
                if (!TextUtils.isEmpty(mHint) && TextUtils.isEmpty(editTextHint)) {
                    // If the hint is disabled, but we have a hint set, and the EditText doesn't,
                    // pass it through...
                    mEditText.setHint(mHint);
                }
                // Now clear out any set hint
                setHintInternal(null);
            } else {
                if (!TextUtils.isEmpty(editTextHint)) {
                    // If the hint is now enabled and the EditText has one set, we'll use it if
                    // we don't already have one, and clear the EditText's
                    if (TextUtils.isEmpty(mHint)) {
                        setHint(editTextHint);
                    }
                    mEditText.setHint(null);
                }
            }

            // Now update the EditText top margin
            if (mEditText != null) {
                updateInputLayoutMargins();
            }
        }
    }

    /**
     * Returns whether the floating label functionality is enabled or not in this layout.
     *
     * @attr ref R.styleable#TextInputLayout_hintEnabled
     * @see #setHintEnabled(boolean)
     */
    public boolean isHintEnabled() {
        return mHintEnabled;
    }

    /**
     * Sets the hint text color, size, style from the specified TextAppearance resource.
     *
     * @attr ref R.styleable#TextInputLayout_hintTextAppearance
     */
    public void setHintTextAppearance(@StyleRes int resId) {
        mCollapsingTextHelper.setCollapsedTextAppearance(resId);
        mFocusedTextColor = mCollapsingTextHelper.getCollapsedTextColor();

        if (mEditText != null) {
            updateLabelState(false);
            // Text size might have changed so update the top margin
            updateInputLayoutMargins();
        }
    }

    private void addIndicator(TextView indicator, int index) {
        if (mIndicatorArea == null && mIndicatorsAdded == 0) {
            mIndicatorArea = new LinearLayout(getContext());
            mIndicatorArea.setOrientation(LinearLayout.HORIZONTAL);
            addView(mIndicatorArea, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            // Add a flexible spacer in the middle so that the left/right views stay pinned
            final Space spacer = new Space(getContext());
            final LinearLayout.LayoutParams spacerLp = new LinearLayout.LayoutParams(0, 0, 1f);
            mIndicatorArea.addView(spacer, spacerLp);

            if (mEditText != null) {
                adjustIndicatorPadding();
            }
        }
        assert mIndicatorArea != null;
        mIndicatorArea.setVisibility(View.VISIBLE);
        mIndicatorArea.addView(indicator, index);
        sharedPrefUtils.applyFont(this, AppUtils.FONTS.HelveticaNeue);

        mIndicatorsAdded++;
    }

    private void adjustIndicatorPadding() {
        // Add padding to the error and character counter so that they match the EditText
//        SharedPrefUtils sharedPrefUtils= new SharedPrefUtils(getContext());
        ViewCompat.setPaddingRelative(mIndicatorArea, ViewCompat.getPaddingStart(mEditText),
                0, ViewCompat.getPaddingEnd(mEditText), mEditText.getPaddingBottom());
    }

    private void removeIndicator(TextView indicator) {
        if (mIndicatorArea != null) {
            mIndicatorArea.removeView(indicator);
            if (--mIndicatorsAdded == 0) {
                mIndicatorArea.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Whether the error functionality is enabled or not in this layout. Enabling this
     * functionality before setting an error message via {@link #setError(CharSequence)}, will mean
     * that this layout will not change size when an error is displayed.
     *
     * @attr ref R.styleable#TextInputLayout_errorEnabled
     */
    @SuppressLint("PrivateResource")
    public void setErrorEnabled(boolean enabled) {
        if (mErrorEnabled != enabled) {
            if (mErrorView != null) {
                mErrorView.animate().cancel();
            }

            if (enabled) {
                mErrorView = new AppCompatTextView(getContext());
                mErrorView.setId(R.id.textinput_error);
                if (mTypeface != null) {
                    mErrorView.setTypeface(mTypeface);
                }
                boolean useDefaultColor = false;
                try {
                    TextViewCompat.setTextAppearance(mErrorView, mErrorTextAppearance);
                    if (Build.VERSION.SDK_INT >= 23 && mErrorView.getTextColors().getDefaultColor() == Color.MAGENTA) {
                        // Caused by our theme not extending from Theme.Design*. On API 23 and
                        // above, unresolved theme attrs result in MAGENTA rather than an exception.
                        // Flag so that we use a decent default
                        useDefaultColor = true;
                    }
                } catch (Exception e) {
                    // Caused by our theme not extending from Theme.Design*. Flag so that we use
                    // a decent default
                    useDefaultColor = true;
                }
                if (useDefaultColor) {
                    // Probably caused by our theme not extending from Theme.Design*. Instead
                    // we manually set something appropriate
                    TextViewCompat.setTextAppearance(mErrorView, R.style.TextAppearance_AppCompat_Caption);
                    mErrorView.setTextColor(ContextCompat.getColor(getContext(), R.color.error_color_material_light));
                }
                mErrorView.setVisibility(INVISIBLE);
                ViewCompat.setAccessibilityLiveRegion(mErrorView, ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
                addIndicator(mErrorView, 0);
            } else {
                mErrorShown = false;
                updateEditTextBackground();
                removeIndicator(mErrorView);
                mErrorView = null;
            }
            mErrorEnabled = enabled;
        }
    }

    /**
     * Sets the text color and size for the error message from the specified
     * TextAppearance resource.
     *
     * @attr ref R.styleable#TextInputLayout_errorTextAppearance
     */
    public void setErrorTextAppearance(@StyleRes int resId) {
        mErrorTextAppearance = resId;
        if (mErrorView != null) {
            TextViewCompat.setTextAppearance(mErrorView, resId);
        }
    }

    /**
     * Returns whether the error functionality is enabled or not in this layout.
     *
     * @attr ref R.styleable#TextInputLayout_errorEnabled
     * @see #setErrorEnabled(boolean)
     */
    public boolean isErrorEnabled() {
        return mErrorEnabled;
    }

    /**
     * Sets an error message that will be displayed below our {@link EditText}. If the
     * {@code error} is {@code null}, the error message will be cleared.
     * <p>
     * If the error functionality has not been enabled via {@link #setErrorEnabled(boolean)}, then
     * it will be automatically enabled if {@code error} is not empty.
     *
     * @param error Error message to display, or null to clear
     * @see #getError()
     */
    public void setError(@Nullable final CharSequence error) {
        // Only animate if we're enabled, laid out, and we have a different error message
        setError(error, ViewCompat.isLaidOut(this) && isEnabled()
                && (mErrorView == null || !TextUtils.equals(mErrorView.getText(), error)));
    }

    private void setError(@Nullable final CharSequence error, final boolean animate) {
        mError = error;

        if (!mErrorEnabled) {
            if (TextUtils.isEmpty(error)) {
                // If error isn't enabled, and the error is empty, just return
                return;
            }
            // Else, we'll assume that they want to enable the error functionality
            setErrorEnabled(true);
        }

        mErrorShown = !TextUtils.isEmpty(error);

        // Cancel any on-going animation
        mErrorView.animate().cancel();

        if (mErrorShown) {
            mErrorView.setText(error);
            mErrorView.setVisibility(VISIBLE);

            if (animate) {
                if (mErrorView.getAlpha() == 1f) {
                    // If it's currently 100% show, we'll animate it from 0
                    mErrorView.setAlpha(0f);
                }
                mErrorView.animate()
                        .alpha(1f)
                        .setDuration(ANIMATION_DURATION)
                        .setInterpolator(CustomAnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animator) {
                                mErrorView.setVisibility(VISIBLE);
                            }
                        }).start();
            } else {
                // Set alpha to 1f, just in case
                mErrorView.setAlpha(1f);
            }
        } else {
            if (mErrorView.getVisibility() == VISIBLE) {
                if (animate) {
                    mErrorView.animate()
                            .alpha(0f)
                            .setDuration(ANIMATION_DURATION)
                            .setInterpolator(CustomAnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    mErrorView.setText(error);
                                    mErrorView.setVisibility(INVISIBLE);
                                }
                            }).start();
                } else {
                    mErrorView.setText(error);
                    mErrorView.setVisibility(INVISIBLE);
                }
            }
        }

        updateEditTextBackground();
        updateLabelState(animate);
    }

    /**
     * Whether the character counter functionality is enabled or not in this layout.
     *
     * @attr ref R.styleable#TextInputLayout_counterEnabled
     */
    @SuppressLint("PrivateResource")
    public void setCounterEnabled(boolean enabled) {
        if (mCounterEnabled != enabled) {
            if (enabled) {
                mCounterView = new AppCompatTextView(getContext());
                mCounterView.setId(R.id.textinput_counter);
                if (mTypeface != null) {
                    mCounterView.setTypeface(mTypeface);
                }
                mCounterView.setMaxLines(1);
                try {
                    TextViewCompat.setTextAppearance(mCounterView, mCounterTextAppearance);
                } catch (Exception e) {
                    // Probably caused by our theme not extending from Theme.Design*. Instead
                    // we manually set something appropriate
                    TextViewCompat.setTextAppearance(mCounterView,
                            R.style.TextAppearance_AppCompat_Caption);
                    mCounterView.setTextColor(ContextCompat.getColor(getContext(),
                            R.color.error_color_material_light));
                }
                addIndicator(mCounterView, -1);
                if (mEditText == null) {
                    updateCounter(0);
                } else {
                    updateCounter(mEditText.getText().length());
                }
            } else {
                removeIndicator(mCounterView);
                mCounterView = null;
            }
            mCounterEnabled = enabled;
        }
    }

    /**
     * Returns whether the character counter functionality is enabled or not in this layout.
     *
     * @attr ref R.styleable#TextInputLayout_counterEnabled
     * @see #setCounterEnabled(boolean)
     */
    public boolean isCounterEnabled() {
        return mCounterEnabled;
    }

    /**
     * Sets the max length to display at the character counter.
     *
     * @param maxLength maxLength to display. Any value less than or equal to 0 will not be shown.
     * @attr ref R.styleable#TextInputLayout_counterMaxLength
     */
    public void setCounterMaxLength(int maxLength) {
        if (mCounterMaxLength != maxLength) {
            if (maxLength > 0) {
                mCounterMaxLength = maxLength;
            } else {
                mCounterMaxLength = INVALID_MAX_LENGTH;
            }
            if (mCounterEnabled) {
                updateCounter(mEditText == null ? 0 : mEditText.getText().length());
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        // Since we're set to addStatesFromChildren, we need to make sure that we set all
        // children to enabled/disabled otherwise any enabled children will wipe out our disabled
        // drawable state
        recursiveSetEnabled(this, enabled);
        super.setEnabled(enabled);
    }

    private static void recursiveSetEnabled(final ViewGroup vg, final boolean enabled) {
        for (int i = 0, count = vg.getChildCount(); i < count; i++) {
            final View child = vg.getChildAt(i);
            child.setEnabled(enabled);
            if (child instanceof ViewGroup) {
                recursiveSetEnabled((ViewGroup) child, enabled);
            }
        }
    }

    /**
     * Returns the max length shown at the character counter.
     *
     * @attr ref R.styleable#TextInputLayout_counterMaxLength
     */
    public int getCounterMaxLength() {
        return mCounterMaxLength;
    }

    @SuppressLint("PrivateResource")
    void updateCounter(int length) {
        boolean wasCounterOverflowed = mCounterOverflowed;
        if (mCounterMaxLength == INVALID_MAX_LENGTH) {
            mCounterView.setText(String.valueOf(length));
            mCounterOverflowed = false;
        } else {
            mCounterOverflowed = length > mCounterMaxLength;
            if (wasCounterOverflowed != mCounterOverflowed) {
                TextViewCompat.setTextAppearance(mCounterView, mCounterOverflowed
                        ? mCounterOverflowTextAppearance : mCounterTextAppearance);
            }
            mCounterView.setText(getContext().getString(R.string.character_counter_pattern,
                    length, mCounterMaxLength));
        }
        if (mEditText != null && wasCounterOverflowed != mCounterOverflowed) {
            updateLabelState(false);
            updateEditTextBackground();
        }
    }

    @SuppressLint("RestrictedApi")
    private void updateEditTextBackground() {
        if (mEditText == null) {
            return;
        }

        Drawable editTextBackground = mEditText.getBackground();
        if (editTextBackground == null) {
            return;
        }

        ensureBackgroundDrawableStateWorkaround();

        if (androidx.appcompat.widget.DrawableUtils.canSafelyMutateDrawable(editTextBackground)) {
            editTextBackground = editTextBackground.mutate();
        }

        if (mErrorShown && mErrorView != null) {
            // Set a color filter of the error color
            editTextBackground.setColorFilter(
                    AppCompatDrawableManager.getPorterDuffColorFilter(
                            mErrorView.getCurrentTextColor(), PorterDuff.Mode.SRC_IN));
        } else if (mCounterOverflowed && mCounterView != null) {
            // Set a color filter of the counter color
            editTextBackground.setColorFilter(
                    AppCompatDrawableManager.getPorterDuffColorFilter(
                            mCounterView.getCurrentTextColor(), PorterDuff.Mode.SRC_IN));
        } else {
            // Else reset the color filter and refresh the drawable state so that the
            // normal tint is used
            DrawableCompat.clearColorFilter(editTextBackground);
            mEditText.refreshDrawableState();
        }
    }

    private void ensureBackgroundDrawableStateWorkaround() {
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk != 21 && sdk != 22) {
            // The workaround is only required on API 21-22
            return;
        }
        final Drawable bg = mEditText.getBackground();
        if (bg == null) {
            return;
        }

        if (!mHasReconstructedEditTextBackground) {
            // This is gross. There is an issue in the platform which affects container Drawables
            // where the first drawable retrieved from resources will propagate any changes
            // (like color filter) to all instances from the cache. We'll try to workaround it...

            final Drawable newBg = bg.getConstantState() != null ? bg.getConstantState().newDrawable() : null;

            if (bg instanceof DrawableContainer) {
                // If we have a Drawable container, we can try and set it's constant state via
                // reflection from the new Drawable
                assert newBg != null;
                mHasReconstructedEditTextBackground =
                        CustomDrawableUtils.setContainerConstantState(
                                (DrawableContainer) bg, newBg.getConstantState());
            }

            if (!mHasReconstructedEditTextBackground) {
                // If we reach here then we just need to set a brand new instance of the Drawable
                // as the background. This has the unfortunate side-effect of wiping out any
                // user set padding, but I'd hope that use of custom padding on an EditText
                // is limited.
                ViewCompat.setBackground(mEditText, newBg);
                mHasReconstructedEditTextBackground = true;
            }
        }
    }

    static class SavedState extends AbsSavedState {
        CharSequence error;
        boolean isPasswordToggledVisible;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            error = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(source);
            isPasswordToggledVisible = (source.readInt() == 1);

        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            TextUtils.writeToParcel(error, dest, flags);
            dest.writeInt(isPasswordToggledVisible ? 1 : 0);
        }

        @Override
        public String toString() {
            return "TextInputLayout.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " error=" + error + "}";
        }

        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            @Override
            public CustomTextInputLayout.SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new CustomTextInputLayout.SavedState(in, loader);
            }

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        if (mErrorShown) {
            ss.error = getError();
        }
        ss.isPasswordToggledVisible = mPasswordToggledVisible;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setError(ss.error);
        if (ss.isPasswordToggledVisible) {
            passwordVisibilityToggleRequested(true);
        }
//        requestLayout();
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        mRestoringSavedState = true;
        super.dispatchRestoreInstanceState(container);
        mRestoringSavedState = false;
    }

    /**
     * Returns the error message that was set to be displayed with
     * {@link #setError(CharSequence)}, or <code>null</code> if no error was set
     * or if error displaying is not enabled.
     *
     * @see #setError(CharSequence)
     */
    @Nullable
    public CharSequence getError() {
        return mErrorEnabled ? mError : null;
    }

    /**
     * Returns whether any hint state changes, due to being focused or non-empty text, are
     * animated.
     *
     * @attr ref R.styleable#TextInputLayout_hintAnimationEnabled
     * @see #setHintAnimationEnabled(boolean)
     */
    public boolean isHintAnimationEnabled() {
        return mHintAnimationEnabled;
    }

    /**
     * Set whether any hint state changes, due to being focused or non-empty text, are
     * animated.
     *
     * @attr ref R.styleable#TextInputLayout_hintAnimationEnabled
     * @see #isHintAnimationEnabled()
     */
    public void setHintAnimationEnabled(boolean enabled) {
        mHintAnimationEnabled = enabled;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (mHintEnabled) {
            mCollapsingTextHelper.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        updatePasswordToggleView();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint({"RestrictedApi", "PrivateResource"})
    private void updatePasswordToggleView() {
        if (mEditText == null) {
            // If there is no EditText, there is nothing to update
            return;
        }

        if (shouldShowPasswordIcon()) {
            if (mPasswordToggleView == null) {
                mPasswordToggleView = (CheckableImageButton) LayoutInflater.from(getContext())
                        .inflate(R.layout.design_text_input_password_icon, mInputFrame, false);
                mPasswordToggleView.setImageDrawable(mPasswordToggleDrawable);
                mPasswordToggleView.setContentDescription(mPasswordToggleContentDesc);
                mInputFrame.addView(mPasswordToggleView);

                mPasswordToggleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        passwordVisibilityToggleRequested(false);
                    }
                });
            }

            if (mEditText != null && ViewCompat.getMinimumHeight(mEditText) <= 0) {
                // We should make sure that the EditText has the same min-height as the password
                // toggle view. This ensure focus works properly, and there is no visual jump
                // if the password toggle is enabled/disabled.
                mEditText.setMinimumHeight(ViewCompat.getMinimumHeight(mPasswordToggleView));
            }

            mPasswordToggleView.setVisibility(VISIBLE);
            mPasswordToggleView.setChecked(mPasswordToggledVisible);

            // We need to add a dummy drawable as the end compound drawable so that the text is
            // indented and doesn't display below the toggle view
            if (mPasswordToggleDummyDrawable == null) {
                mPasswordToggleDummyDrawable = new ColorDrawable();
            }
            mPasswordToggleDummyDrawable.setBounds(0, 0, mPasswordToggleView.getMeasuredWidth(), 1);

            final Drawable[] compounds = TextViewCompat.getCompoundDrawablesRelative(mEditText);
            // Store the user defined end compound drawable so that we can restore it later
            if (compounds[2] != mPasswordToggleDummyDrawable) {
                mOriginalEditTextEndDrawable = compounds[2];
            }
            TextViewCompat.setCompoundDrawablesRelative(mEditText, compounds[0], compounds[1],
                    mPasswordToggleDummyDrawable, compounds[3]);

            // Copy over the EditText's padding so that we match
            mPasswordToggleView.setPadding(mEditText.getPaddingLeft(),
                    mEditText.getPaddingTop(), mEditText.getPaddingRight(),
                    mEditText.getPaddingBottom());
        } else {
            if (mPasswordToggleView != null && mPasswordToggleView.getVisibility() == VISIBLE) {
                mPasswordToggleView.setVisibility(View.GONE);
            }

            if (mPasswordToggleDummyDrawable != null) {
                // Make sure that we remove the dummy end compound drawable if it exists, and then
                // clear it
                final Drawable[] compounds = TextViewCompat.getCompoundDrawablesRelative(mEditText);
                if (compounds[2] == mPasswordToggleDummyDrawable) {
                    TextViewCompat.setCompoundDrawablesRelative(mEditText, compounds[0],
                            compounds[1], mOriginalEditTextEndDrawable, compounds[3]);
                    mPasswordToggleDummyDrawable = null;
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private void passwordVisibilityToggleRequested(boolean shouldSkipAnimations) {
        if (mPasswordToggleEnabled) {
            // Store the current cursor position
            final int selection = mEditText.getSelectionEnd();

            if (hasPasswordTransformation()) {
                mEditText.setTransformationMethod(null);
                mPasswordToggledVisible = true;
            } else {
                mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mPasswordToggledVisible = false;
            }

            mPasswordToggleView.setChecked(mPasswordToggledVisible);
            if (shouldSkipAnimations) {
                mPasswordToggleView.jumpDrawablesToCurrentState();
            }

            // And restore the cursor position
            mEditText.setSelection(selection);
        }
    }

    private boolean hasPasswordTransformation() {
        return mEditText != null
                && mEditText.getTransformationMethod() instanceof PasswordTransformationMethod;
    }

    private boolean shouldShowPasswordIcon() {
        return mPasswordToggleEnabled && (hasPasswordTransformation() || mPasswordToggledVisible);
    }

    private void applyPasswordToggleTint() {
        if (mPasswordToggleDrawable != null
                && (mHasPasswordToggleTintList || mHasPasswordToggleTintMode)) {
            mPasswordToggleDrawable = DrawableCompat.wrap(mPasswordToggleDrawable).mutate();

            if (mHasPasswordToggleTintList) {
                DrawableCompat.setTintList(mPasswordToggleDrawable, mPasswordToggleTintList);
            }
            if (mHasPasswordToggleTintMode) {
                DrawableCompat.setTintMode(mPasswordToggleDrawable, mPasswordToggleTintMode);
            }

            if (mPasswordToggleView != null
                    && mPasswordToggleView.getDrawable() != mPasswordToggleDrawable) {
                mPasswordToggleView.setImageDrawable(mPasswordToggleDrawable);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mHintEnabled && mEditText != null) {
            final Rect rect = mTmpRect;
            ViewGroupUtils.getDescendantRect(this, mEditText, rect);

            final int l = rect.left + mEditText.getCompoundPaddingLeft();
            final int r = rect.right - mEditText.getCompoundPaddingRight();

            mCollapsingTextHelper.setExpandedBounds(
                    l, rect.top + mEditText.getCompoundPaddingTop(),
                    r, rect.bottom - mEditText.getCompoundPaddingBottom());

            // Set the collapsed bounds to be the the full height (minus padding) to match the
            // EditText's editable area
            mCollapsingTextHelper.setCollapsedBounds(l, getPaddingTop(),
                    r, bottom - top - getPaddingBottom());

            mCollapsingTextHelper.recalculate();
        }
    }

    private void collapseHint(boolean animate) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        if (animate && mHintAnimationEnabled) {
            animateToExpansionFraction(1f);
        } else {
            mCollapsingTextHelper.setExpansionFraction(1f);
        }
        mHintExpanded = false;
    }

    @Override
    protected void drawableStateChanged() {
        if (mInDrawableStateChanged) {
            // Some of the calls below will update the drawable state of child views. Since we're
            // using addStatesFromChildren we can get into infinite recursion, hence we'll just
            // exit in this instance
            return;
        }

        mInDrawableStateChanged = true;

        super.drawableStateChanged();

        final int[] state = getDrawableState();
        boolean changed = false;

        // Drawable state has changed so see if we need to update the label
        updateLabelState(ViewCompat.isLaidOut(this) && isEnabled());

        updateEditTextBackground();

        changed = mCollapsingTextHelper.setState(state);

        if (changed) {
            invalidate();
        }

        mInDrawableStateChanged = false;
    }

    private void expandHint(boolean animate) {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
        if (animate && mHintAnimationEnabled) {
            animateToExpansionFraction(0f);
        } else {
            mCollapsingTextHelper.setExpansionFraction(0f);
        }
        mHintExpanded = true;
    }

    @VisibleForTesting
    void animateToExpansionFraction(final float target) {
        if (mCollapsingTextHelper.getExpansionFraction() == target) {
            return;
        }
        if (mAnimator == null) {
            mAnimator = new ValueAnimator();
            mAnimator.setInterpolator(CustomAnimationUtils.LINEAR_INTERPOLATOR);
            mAnimator.setDuration(ANIMATION_DURATION);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    mCollapsingTextHelper.setExpansionFraction((float) animator.getAnimatedValue());
                }
            });
        }
        mAnimator.setFloatValues(mCollapsingTextHelper.getExpansionFraction(), target);
        mAnimator.start();
    }

    private class TextInputAccessibilityDelegate extends AccessibilityDelegateCompat {
        TextInputAccessibilityDelegate() {
        }

        @Override
        public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(host, event);
            event.setClassName(CustomTextInputLayout.class.getSimpleName());
        }

        @Override
        public void onPopulateAccessibilityEvent(View host, AccessibilityEvent event) {
            super.onPopulateAccessibilityEvent(host, event);

            final CharSequence text = mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                event.getText().add(text);
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setClassName(CustomTextInputLayout.class.getSimpleName());

            final CharSequence text = mCollapsingTextHelper.getText();
            if (!TextUtils.isEmpty(text)) {
                info.setText(text);
            }
            if (mEditText != null) {
                info.setLabelFor(mEditText);
            }
            final CharSequence error = mErrorView != null ? mErrorView.getText() : null;
            if (!TextUtils.isEmpty(error)) {
                info.setContentInvalid(true);
                info.setError(error);
            }
        }
    }

    private static boolean arrayContains(int[] array, int value) {
        for (int v : array) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }
}
