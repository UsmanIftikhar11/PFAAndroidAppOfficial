package com.pfa.pfaapp.interfaces;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * while typing CNIC Number, replace the 6th and 13 character with (-) for formatting
 * <p>
 * //@param view     which view is in focus
 */

public class CNICTextWatcher implements TextWatcher {
    private int charCount;
    private EditText editText;
    private String fieldType;
    private SendMessageCallback callback;

    public CNICTextWatcher(final EditText editText, String fieldType, SendMessageCallback callback, int charCount) {
        this.editText = editText;
        this.fieldType = fieldType;
        this.callback = callback;
        this.charCount = charCount;



    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    private final String checkText = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-";
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String message = editText.getText().toString();


        if (charCount < message.length()) {


            if (fieldType != null && fieldType.contains("cnic")) {


                if (message.endsWith("-") && message.length() != 6 && message.length() != 14) {
                    message = message.substring(0, message.length() - 1);
                    editText.setText(message);
                    editText.setSelection(message.length());
                } else if ((!checkText.contains("" + message.charAt(message.length() - 1))) && message.length() != 6 && message.length() != 14) {
                    message = message.substring(0, message.length() - 1);
                    editText.setText(message);
                    editText.setSelection(message.length());
                } else if (message.length() == 6) {
                    message = new StringBuilder(message).insert(5, "-").toString();
                    editText.setText(message);
                    editText.setSelection(message.length());
                } else if (message.length() == 14) {
                    message = new StringBuilder(message).insert(13, "-").toString();
                    editText.setText(message);
                    editText.setSelection(message.length());
                }

            }

        }

        charCount = message.length();
        callback.sendMsg(message);
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
