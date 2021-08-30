package com.pfa.pfaapp.interfaces;

import android.text.Editable;
import android.text.TextWatcher;

public class PFATextWatcher implements TextWatcher {
    private SendMessageCallback callback;

    public PFATextWatcher(SendMessageCallback callback) {
        this.callback = callback;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        callback.sendMsg(s.toString());

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
