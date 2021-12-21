package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pfa.pfaapp.R;
import com.pfa.pfaapp.httputils.ImageHttpUtils;
import com.pfa.pfaapp.interfaces.CNICTextWatcher;
import com.pfa.pfaapp.interfaces.ImageCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.models.FormDataInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import java.util.HashMap;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * PFAEditText is a custom Edittext that can be single line or multi line EditText.
 * It has memeber params like @{@link FormFieldInfo}, @{@link AppUtils}, @List<{@link FormDataInfo}></{@link> and Custom Text Input Layout @textInputLayout[{@link PFATextInputLayout}
 * {@link PFAEditText} is responsible for all the text input fields to be added to form dynamically
 */
@SuppressLint("ViewConstructor")
public class PFAEditText extends ClearableEditText {
    private FormFieldInfo formFieldInfo;
    private Context mContext;
    private SharedPrefUtils appUtils;
    HashMap<String, List<FormDataInfo>> formViewsData;
    protected PFATextInputLayout textInputLayout;
    private KeyListener keyListener;

    public PFAEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.appUtils = new SharedPrefUtils(context);
    }

    public PFAEditText(Context mContext) {
        super(mContext);
    }

    public PFAEditText(Context context, FormFieldInfo formFieldInfo) {
        this(context, formFieldInfo, null);
    }

    /**
     * initialise the class {@PFAEditText}and set the context, formFieldInfo, formViewsData
     *
     * @param context       {@link Context}
     * @param formFieldInfo {@link FormFieldInfo}
     * @param formViewsData Hashmap of {@link FormDataInfo} where key is the field tag/name and List<{@link FormDataInfo}></{@link> is the list of all the values.
     */
    public PFAEditText(Context context, FormFieldInfo formFieldInfo, HashMap<String, List<FormDataInfo>> formViewsData) {
        super(context);
        this.appUtils = new SharedPrefUtils(context);
        this.mContext = context;
        this.formFieldInfo = formFieldInfo;
        this.formViewsData = formViewsData;

        setInitialData();


    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (formFieldInfo != null && formFieldInfo.getField_type() != null && formFieldInfo.getField_type().contains("cnic")) {
            setSelection(getText().length());
        }
    }

    private void setInitialData() {
        setProperties();
        if (formFieldInfo.isNotEditable()) {
            setNotEditable(true);
        }
    }

    @SuppressLint("RtlHardcoded")
    private void setProperties() {

        setSingleLine();
        setPadding(appUtils.convertDpToPixel(20), appUtils.convertDpToPixel(0), 0, appUtils.convertDpToPixel(0));
        setCompoundDrawablePadding(appUtils.convertDpToPixel(7));
        setTag(formFieldInfo.getField_name());
        setHint(appUtils.isEnglishLang() ? formFieldInfo.getValue() : formFieldInfo.getValueUrdu());

        if (formFieldInfo.getField_type().contains("cnic")) {
            setMaxEms(15);
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(15), new EmojiExcludeFilter()});

        } else if (formFieldInfo.getField_type().contains("phone")) {
            setMaxEms(11);
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(11), new EmojiExcludeFilter()});

        } else {
            if (formFieldInfo.getMin_limit() > 0) {
                setMinEms(formFieldInfo.getMin_limit());
            }
            if (formFieldInfo.getMax_limit() > 0) {
                setMaxEms(formFieldInfo.getMax_limit());
                setFilters(new InputFilter[]{new InputFilter.LengthFilter(formFieldInfo.getMax_limit()), new EmojiExcludeFilter()});
            } else
                setFilters(new InputFilter[]{new EmojiExcludeFilter()});

        }

        appUtils.applyFont(this, AppUtils.FONTS.HelveticaNeueMedium);
        appUtils.applyStyle(formFieldInfo.getFont_style(), formFieldInfo.getFont_size(), formFieldInfo.getFont_color(), this);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (formFieldInfo.getField_type().equalsIgnoreCase("textarea")) {
            setTextAreaLines();
        } else if (formFieldInfo.getField_type().equalsIgnoreCase("abc")) {
            setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        } else {
            setSingleLine();
        }

        if (!formFieldInfo.getField_type().equalsIgnoreCase("abc")) {

            params.setMargins(0, 0, (int) mContext.getResources().getDimension(R.dimen.form_top_margin), 0);

        }
        setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT | Gravity.START);

        setLayoutParams(params);
        setEmeSizeAndDrawable();
        populateData();
        setTextColor(mContext.getResources().getColor(R.color.black));
        setTextSize(COMPLEX_UNIT_SP, 15);
        setHintTextColor(mContext.getResources().getColor(R.color.hint_color));

        if (formViewsData != null && formViewsData.containsKey(getTag().toString())) {
            setText(formViewsData.get(getTag().toString()).get(0).getValue());
        } else if (formFieldInfo != null && formFieldInfo.getData() != null && formFieldInfo.getData().size() > 0) {
            setText(formFieldInfo.getData().get(0).getKey());
        }


        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    setHint("");
                    if (textInputLayout != null)
                        textInputLayout.setError(null);
                } else {
                    setHint(appUtils.isEnglishLang() ? formFieldInfo.getValue() : formFieldInfo.getValueUrdu());
                    if (getText().toString().length() > 0) {
                        showInvalidFormError(true);
                        Log.d("edittextError", "showError 3= " );
                    }
                }
            }
        });
    }

    public void setNotEditable(boolean isNotEditable) {
        if (keyListener == null)
            keyListener = getKeyListener();

        if (isNotEditable) {

            setEnabled(false);
            setKeyListener(null);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setFocusable(NOT_FOCUSABLE);
            } else {
                setFocusable(false);
            }

        } else {
            setEnabled(true);
            setKeyListener(keyListener);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setFocusable(FOCUSABLE);
            } else {
                setFocusable(true);
            }
            setFocusableInTouchMode(true);
        }
    }

    public void showHideDropDown(boolean show){
        if (show)
            textInputLayout.setVisibility(VISIBLE);
        else
            textInputLayout.setVisibility(GONE);
    }

    public void setRequired(boolean required){
        if (required) {
            formFieldInfo.setRequired(true);
//            formFieldInfo.setInvisible(true);
        }
        else {
            formFieldInfo.setRequired(false);
//            formFieldInfo.setInvisible(false);
        }
    }


    public FormDataInfo getETData(boolean showError) {

        FormDataInfo formDataInfo = new FormDataInfo();
        if (formFieldInfo != null && formFieldInfo.getData() != null && formFieldInfo.getData().size() >= 1) {
//            String fieldName= formFieldInfo.getField_name();
//            Log.e("Field Name",fieldName);
            formDataInfo = formFieldInfo.getData().get(0);
        } else {
            formDataInfo.setName(getTag().toString());
//            formDataInfo.setValue(getText().toString());
        }

        formDataInfo.setValue(getText().toString());
        formDataInfo.setKey(getText().toString());

        if (formFieldInfo != null && formFieldInfo.getField_type() != null && formFieldInfo.getField_type().contains("cnic")) {

            formDataInfo.setKey(formDataInfo.getKey().replaceAll("-", ""));
        }
        showInvalidFormError(showError);
        Log.d("edittextError" , "showError 1= " + showError);

        return formDataInfo;
    }

    public FormFieldInfo getFormFieldInfo() {
        return formFieldInfo;
    }

    private void populateData() {
        if (formFieldInfo != null && formFieldInfo.getData() != null && formFieldInfo.getData().size() >= 1) {
            setText(formFieldInfo.getData().get(0).getValue());
        }
    }

    private void setEmeSizeAndDrawable() {

        addTextChangedListener(new CNICTextWatcher(this, formFieldInfo.getField_type(), new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {

            }
        }, formFieldInfo.getMax_limit()));

        // download and set drawable left on edit text field
        if (formFieldInfo != null && formFieldInfo.getIcon() != null && !formFieldInfo.getIcon().equals("") && (!formFieldInfo.getIcon().equalsIgnoreCase("http://www.jazzcash.com.pk/assets/uploads/2016/05/new-icon-set-CNIC.png")))
            new ImageHttpUtils(mContext, formFieldInfo.getIcon(), new ImageCallback() {
                @Override
                public void onBitmapDownloaded(Bitmap bitmap) {
                    Drawable drawableLeft = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, appUtils.convertDpToPixel(30), appUtils.convertDpToPixel(30), true));

                    if (formFieldInfo.getField_type().equalsIgnoreCase("textarea")) {

                        GravityCompoundDrawable gravityDrawable = new GravityCompoundDrawable(drawableLeft);
                        drawableLeft.setBounds(0, 0, drawableLeft.getIntrinsicWidth(), drawableLeft.getIntrinsicHeight());
                        gravityDrawable.setBounds(0, 0, drawableLeft.getIntrinsicWidth(), drawableLeft.getIntrinsicHeight());
                        if (appUtils.isEnglishLang()) {
                            setCompoundDrawables(gravityDrawable, null, null, null);
                        } else {
                            setCompoundDrawables(null, null, gravityDrawable, null);
                        }

                    } else {
                        if (appUtils.isEnglishLang()) {
                            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
                        } else {
                            setCompoundDrawablesWithIntrinsicBounds(null, null, drawableLeft, null);
                        }
                    }
                }
            });
    }

    private void showInvalidFormError(boolean showError) {
        Log.d("edittextError" , "showError1 = " + showError);
        if (formFieldInfo != null && formFieldInfo.isRequired()) {
            showError(showError);
        } else if (!getText().toString().isEmpty()) {
            showError(showError);
        } else if (textInputLayout != null) {
            textInputLayout.setError(null);
        }
    }

    protected void showError(boolean showError) {
        if (formFieldInfo != null && formFieldInfo.getField_type() != null && formFieldInfo.getField_type().contains("cnic")) {

            String cnicNum = getText().toString().replaceAll("-", "");
            if (cnicNum.length() < 13) {
                if (showError)
                    textInputLayout.setError(mContext.getString(R.string.invalid_cnic));
            } else {
                textInputLayout.setError(null);
            }
        } else if (formFieldInfo != null && formFieldInfo.getField_type() != null && formFieldInfo.getField_type().equalsIgnoreCase("phone")) {
            String phoneNum = getText().toString();
            if ((!phoneNum.startsWith("03")) || (phoneNum.length() < 11)) {
                if (showError)
                    textInputLayout.setError(mContext.getString(R.string.invalid_phone_num_msg));
            } else {
                textInputLayout.setError(null);
            }
        } else if (formFieldInfo != null && formFieldInfo.getField_type() != null && formFieldInfo.getField_type().equalsIgnoreCase("email")) {
            if (appUtils.isInvalidEmail(getText().toString())) {
                if (showError)
                    textInputLayout.setError(mContext.getString(R.string.invalid_email));
            } else {
                textInputLayout.setError(null);
            }
        } else if (getText().toString().isEmpty()) {
            if (textInputLayout != null && showError)
                textInputLayout.setError(mContext.getString(R.string.required_field));
        } else {
            if (textInputLayout != null)
                textInputLayout.setError(null);
        }
    }

    @SuppressLint("RtlHardcoded")
    private void setTextAreaLines() {
        setSingleLine(false);
//        setMaxLines(10);
//        setLines(10);
        setPadding(appUtils.convertDpToPixel(20), appUtils.convertDpToPixel(7), 0, appUtils.convertDpToPixel(7));

        setMinHeight((int) mContext.getResources().getDimension(R.dimen.form_text_area_height));
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);

        populateData();
    }

    public void setData(Context mContext, FormFieldInfo data, HashMap<String, List<FormDataInfo>> formFilteredData) {
        this.mContext = mContext;
        this.formFieldInfo = data;
        this.formViewsData = formFilteredData;
        setInitialData();
    }

    public void addTextInputLayout(PFATextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
        if (formFieldInfo.isNotEditable()) {
            setBackgroundColor(getContext().getResources().getColor(R.color.chat_list_footer_bg));

        } else
            setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        if (formFieldInfo.isRequired()) {
            if (textInputLayout != null) {
                textInputLayout.setBackground(mContext.getResources().getDrawable(appUtils.isEnglishLang() ? R.mipmap.text_bg_star : R.mipmap.ur_text_bg_star));
                textInputLayout.setGravity(Gravity.CENTER_VERTICAL);
            } else {
                setBackgroundDrawable(mContext.getResources().getDrawable(appUtils.isEnglishLang() ? R.mipmap.text_bg_star : R.mipmap.ur_text_bg_star));
            }
        } else {
            if (textInputLayout != null) {
                textInputLayout.setBackground(mContext.getResources().getDrawable(R.mipmap.text_bg));
                textInputLayout.setGravity(Gravity.CENTER_VERTICAL);
            } else {
                setBackgroundDrawable(mContext.getResources().getDrawable(R.mipmap.text_bg));
            }
        }
    }

    public void setSearchOption(final SendMessageCallback callback) {
        setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (callback != null) {
                        callback.sendMsg("search");
                    }

                    return true;
                }
                return false;
            }
        });
    }

}
