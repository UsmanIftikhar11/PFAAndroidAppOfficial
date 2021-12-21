package com.pfa.pfaapp.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.LocalFormsActivity;
import com.pfa.pfaapp.MapsActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.PFAListItem;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.localdbmodels.InspectionInfo;
import com.pfa.pfaapp.models.PFATableInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.pfa.pfaapp.utils.AppConst.CANCEL;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_LATLNG_STR;

public class InspectionAdapter extends BaseAdapter {

    private final List<InspectionInfo> inspectionInfos;

    private final BaseActivity baseActivity;
    private final List<List<PFATableInfo>> suggestions;
    private final PFAListItem pfaListItem;
    private final List<String> columnTags = new ArrayList<>();
    private final SendMessageCallback deleteCallback;
    private boolean isClicked = false;
    private final ProgressDialog progressDialog;

    public InspectionAdapter(BaseActivity context, List<InspectionInfo> inspectionInfos, List<List<PFATableInfo>> data, SendMessageCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
        this.baseActivity = context;
        this.inspectionInfos = inspectionInfos;
        this.suggestions = data;
        pfaListItem = new PFAListItem(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading Draft");
        progressDialog.setMessage("Please wait... ");

    }

    @Override
    public int getCount() {
        return inspectionInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            convertView = pfaListItem.createViews(suggestions.get(0), columnTags, false);
            convertView.setBackground(baseActivity.getResources().getDrawable(R.drawable.list_item_selector));

            ((ImageButton) convertView.findViewById(R.id.downloadImgBtn)).setImageResource(R.mipmap.download_cancel);
            convertView.findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
        }

        if (position <= suggestions.size()) {
            final List<PFATableInfo> columnsData = suggestions.get(position);

            for (int i = 0; i < columnsData.size(); i++) {
                View view = convertView.findViewWithTag(columnsData.get(i).getField_name());
                if (view instanceof SimpleDraweeView) {
                    SimpleDraweeView customNetworkImageView = (SimpleDraweeView) view;
                    if (columnsData.get(i).getIcon() == null || (columnsData.get(i).getIcon().trim().isEmpty())) {
                        view.setVisibility(View.GONE);
                    } else {
                        customNetworkImageView.setVisibility(View.VISIBLE);
                        customNetworkImageView.setImageURI(columnsData.get(i).getIcon());
                    }

                    if (columnsData.get(i).getLat_lng() != null && (!columnsData.get(i).getLat_lng().isEmpty())) {
                        final PFATableInfo temPfaTableInfo = columnsData.get(i);
                        customNetworkImageView.setOnClickListener(v -> {
                            Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_LATLNG_STR, temPfaTableInfo.getLat_lng());
                            baseActivity.sharedPrefUtils.startNewActivity(MapsActivity.class, bundle, false);
                        });
                    }

                } else {
                    TextView textView = (TextView) view;
                    if (textView != null) {

                        if (columnsData.get(i).getData() == null || columnsData.get(i).getData().isEmpty()) {
                            textView.setVisibility(View.GONE);
                        } else {
                            textView.setVisibility(View.VISIBLE);
                        }

                        if (textView.getTag().toString().equalsIgnoreCase("draft_time")) {
                            textView.setText(String.format(Locale.getDefault(), "Draft expiry: %s", inspectionInfos.get(position).getInsert_time()));
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            textView.setText(Html.fromHtml("" + (baseActivity.sharedPrefUtils.isEnglishLang()?columnsData.get(i).getData():columnsData.get(i).getDataUrdu())));
                        }

                    }
                }
            }
        }

        convertView.findViewById(R.id.downloadImgBtn).setOnClickListener(v -> baseActivity.sharedPrefUtils.
                showTwoBtnsMsgDialog("Are you sure you want to delete draft inspection?", message -> {
            if (message.equalsIgnoreCase(CANCEL))
                return;
            deleteCallback.sendMsg("" + position);
        }));

        convertView.setOnClickListener(v -> {
            if (isClicked) {
                return;
            }

            isClicked = true;

            (new Handler()).postDelayed(() -> isClicked = false, 100);


            progressDialog.show();

//                Bundle bundle = new Bundle();
//                bundle.putSerializable(EXTRA_INSPECTION_DATA, inspectionInfos.get(position));
//                bundle.putBoolean("isDraft",true);

            SharedPreferences appSharedPrefs = baseActivity.getSharedPreferences("AppPref" , Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(inspectionInfos);
            prefsEditor.putString("inspec", json);
            prefsEditor.apply();
            baseActivity.getSharedPreferences("appPrefs" , Context.MODE_PRIVATE).edit().putBoolean("isDraft" , true).apply();
            baseActivity.getSharedPreferences("appPrefs" , Context.MODE_PRIVATE).edit().putInt("inspecPos" , position).apply();

            Log.d("BundleData" , "data1 = " + inspectionInfos.get(position).getMenuData());

            baseActivity.sharedPrefUtils.startNewActivity1(LocalFormsActivity.class, true ,false);

            new Handler().postDelayed(progressDialog::dismiss, 2000);
        });

        return convertView;
    }

}
