package com.pfa.pfaapp.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.CreateViewCallback;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONObject;

import java.util.HashMap;

class CustomAddLabelUtils extends SharedPrefUtils {

    JSONObject add_label_application_form;
    private int addLabelCount;
    private CreateViewCallback createViewCallback;

    CustomAddLabelUtils(Context mContext) {
        super(mContext);
    }

    void setAddDynamicItemJSONObj(final String addNewUrl, final LinearLayout addDynamicSubItem, CreateViewCallback createViewCallback) {
        this.createViewCallback=createViewCallback;
        HttpService httpService = new HttpService(mContext);
        httpService.getListsData(addNewUrl, new HashMap<String, String>(), new HttpResponseCallback() {
            @Override
            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                if (response != null && response.optBoolean("status")) {
                    if (response.optJSONObject("data").optJSONArray("form").length() == 1) {
                        add_label_application_form = response.optJSONObject("data").optJSONArray("form").optJSONObject(0);

                        if (addDynamicSubItem != null)
                            setViewForms(addDynamicSubItem);
                        addLabelCount++;
                    }
                }
            }
        }, true);
    }

    @SuppressLint("RtlHardcoded")
    void setViewForms(final LinearLayout menuFragParentLL) {

        if (add_label_application_form != null) {
            FormSectionInfo tempFormSection = new Gson().fromJson(add_label_application_form.toString(), FormSectionInfo.class);

            tempFormSection.setSection_id("" + addLabelCount);

            if (tempFormSection.getFields() != null && tempFormSection.getFields().size() > 0) {
                for (int i = 0; i < tempFormSection.getFields().size(); i++) {
                    tempFormSection.getFields().get(i).setField_name(tempFormSection.getFields().get(i).getField_name() + "[" + addLabelCount + "]");

                    if (tempFormSection.getFields().get(i).getData() != null && tempFormSection.getFields().get(i).getData().size() > 0) {
                        for (int j = 0; j < tempFormSection.getFields().get(i).getData().size(); j++) {
                            tempFormSection.getFields().get(i).getData().get(j).setName(tempFormSection.getFields().get(i).getData().get(j).getName() + "[" + addLabelCount + "]");
                        }
                    }
                }
            }

            final LinearLayout addDynamicSubItem = new LinearLayout(mContext);
            LinearLayout.LayoutParams dynamicSubItemParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addDynamicSubItem.setOrientation(LinearLayout.VERTICAL);
            addDynamicSubItem.setLayoutParams(dynamicSubItemParams);
            dynamicSubItemParams.setMargins(0, convertDpToPixel(7), 0, 0);
            addDynamicSubItem.setPadding(convertDpToPixel(5),0,convertDpToPixel(5),0);

            addDynamicSubItem.setTag("addLabel" + addLabelCount);
            addDynamicSubItem.setBackgroundResource(R.color.disable_color);

            menuFragParentLL.addView(addDynamicSubItem);

            ImageButton deleteImgBtn = new ImageButton(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT | Gravity.END;
            params.setMargins(0, convertDpToPixel(10), convertDpToPixel(10), 0);
            deleteImgBtn.setLayoutParams(params);
            deleteImgBtn.setImageResource(R.mipmap.delete);
            deleteImgBtn.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            addDynamicSubItem.addView(deleteImgBtn);

            if(createViewCallback!=null)
                createViewCallback.createAddView(tempFormSection,addDynamicSubItem);

            deleteImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout layout = (LinearLayout) v.getParent();
                    if (layout != null)
                        menuFragParentLL.removeView(layout);
                }
            });

            addLabelCount++;
        }
    }
}