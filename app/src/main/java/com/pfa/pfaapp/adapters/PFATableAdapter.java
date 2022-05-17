package com.pfa.pfaapp.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.ImageGalleryActivity;
import com.pfa.pfaapp.LocalFormsActivity;
import com.pfa.pfaapp.MapsActivity;
import com.pfa.pfaapp.PFAAddNewActivity;
import com.pfa.pfaapp.PFADetailActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.LocalListLL;
import com.pfa.pfaapp.customviews.PFAListItem;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.localdbmodels.InspectionInfo;
import com.pfa.pfaapp.models.FormFieldInfo;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.printing.PrinterActivity;
import com.pfa.pfaapp.utils.AppUtils;
import com.pfa.pfaapp.utils.LocalFormDialog;
import com.pfa.pfaapp.utils.SharedPrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static android.graphics.Color.parseColor;
import static android.view.View.GONE;
import static com.pfa.pfaapp.utils.AppConst.CANCEL;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTIVITY_TITLE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_DOWNLOAD_URL;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_IMAGES_LIST;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_IMAGE_POSITION;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_LATLNG_STR;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.RC_REFRESH_LIST;

public class PFATableAdapter extends BaseAdapter implements Filterable {

    private final PFAListItem pfaListItem;

    private final List<String> columnTags = new ArrayList<>();
    private final List<List<PFATableInfo>> suggestions = new ArrayList<>();
    private List<List<PFATableInfo>> originalList;
    private final Filter filter = new CustomFilter();
    private final BaseActivity baseActivity;
    private boolean showDeleteIcon;
    private final WhichItemClicked whichItemClicked;
    private boolean showDeseize;
    boolean conducted_inspection;
    String print_data;
    ImageView printB;
    String section_name;
    String print, share;
    int j = 1;

    private boolean isClicked = false;

    public PFATableAdapter(BaseActivity context, String print_data, ImageView printbutton, String sec_name, boolean conducted_inspection, List<List<PFATableInfo>> data, boolean showDeleteIcon, WhichItemClicked whichItemClicked) {
        this.baseActivity = context;
        if (data == null)
            data = new ArrayList<>();
        this.whichItemClicked = whichItemClicked;
        this.showDeleteIcon = showDeleteIcon;
        this.originalList = data;
        this.section_name = sec_name;
        this.printB = printbutton;
        this.conducted_inspection = conducted_inspection;
        this.print_data = print_data;
        suggestions.addAll(originalList);
        pfaListItem = new PFAListItem(context);
        Log.d("viewCreated", "PFATableAdapter");

    }

    public PFATableAdapter(BaseActivity context, List<List<PFATableInfo>> data, boolean showDeleteIcon, WhichItemClicked whichItemClicked) {
        this.baseActivity = context;
        if (data == null)
            data = new ArrayList<>();
        this.whichItemClicked = whichItemClicked;
        this.showDeleteIcon = showDeleteIcon;
        this.originalList = data;
        suggestions.addAll(originalList);
        pfaListItem = new PFAListItem(context);

    }

    public PFATableAdapter(BaseActivity context, List<List<PFATableInfo>> data, WhichItemClicked whichItemClicked, boolean showDeseize) {
        this.baseActivity = context;
        if (data == null)
            data = new ArrayList<>();
        this.whichItemClicked = whichItemClicked;
        this.showDeseize = showDeseize;
        this.originalList = data;
        suggestions.addAll(originalList);
        pfaListItem = new PFAListItem(context);

    }

    public void updateAdapter(List<List<PFATableInfo>> data) {
        this.originalList = data;
        if (suggestions != null)
            suggestions.clear();
        assert suggestions != null;
        suggestions.addAll(originalList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (convertView == null) {
            convertView = pfaListItem.createViews(suggestions.get(0), columnTags, false);


            if (showDeleteIcon) {
                convertView.findViewById(R.id.deleteImgBtn).setVisibility(View.VISIBLE);
            }
            if (showDeseize) {
                convertView.findViewById(R.id.deseizeBtn).setVisibility(View.VISIBLE);
            }

            if (suggestions.get(0).get(0).getDelete_url() != null && (!suggestions.get(0).get(0).getDelete_url().isEmpty())) {
                ((ImageButton) convertView.findViewById(R.id.deleteImgBtn)).setImageResource(R.mipmap.download_cancel);
                convertView.findViewById(R.id.deleteImgBtn).setVisibility(View.VISIBLE);
            }
            convertView.setBackground(baseActivity.getResources().getDrawable(R.drawable.list_item_selector));
        }


        final List<PFATableInfo> columnsData = suggestions.get(position);


        for (int i = 0; i < columnsData.size(); i++) {
            View view = convertView.findViewWithTag(columnsData.get(i).getField_name());

            if (view instanceof SimpleDraweeView) {
                SimpleDraweeView customNetworkImageView = (SimpleDraweeView) view;
                if (columnsData.get(i).getIcon() == null || (columnsData.get(i).getIcon().trim().isEmpty())) {
                    customNetworkImageView.setVisibility(GONE);
                } else {
                    customNetworkImageView.setVisibility(View.VISIBLE);
                    if (columnsData.get(i).getIcon().startsWith("http")) {
                        customNetworkImageView.setImageURI(columnsData.get(i).getIcon());
                    } else {
                        File file = new File(columnsData.get(i).getIcon());
                        customNetworkImageView.setImageURI(Uri.fromFile(file));
                    }
                }

                if (columnsData.get(i).getLat_lng() != null && (!columnsData.get(i).getLat_lng().isEmpty())) {
                    final PFATableInfo temPfaTableInfo = columnsData.get(i);
                    customNetworkImageView.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_ACTIVITY_TITLE, baseActivity.sharedPrefUtils.isEnglishLang() ? suggestions.get(position).get(0).getValue() : suggestions.get(position).get(0).getValueUrdu());
                        bundle.putString(EXTRA_LATLNG_STR, temPfaTableInfo.getLat_lng());
                        baseActivity.sharedPrefUtils.startNewActivity(MapsActivity.class, bundle, false);
                    });
                } else {
                    final int finalI = i;
                    List<String>  imagesListData = new ArrayList<>();
                    for (int i1 = 0 ; i1 < columnsData.size() ; i1++){
                        imagesListData.add(columnsData.get(i1).getData());
                    }
                    customNetworkImageView.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_DOWNLOAD_URL, columnsData.get(finalI).getIcon());
                        bundle.putString(EXTRA_IMAGE_POSITION, String.valueOf(finalI));
                        bundle.putStringArrayList(EXTRA_IMAGES_LIST, (ArrayList<String>) imagesListData);
                        baseActivity.sharedPrefUtils.startNewActivity(ImageGalleryActivity.class, bundle, false);
                    });
                }
            } else {
                final TextView textView = (TextView) view;
                if (textView != null) {
                    if (columnsData.get(i).getData() == null || columnsData.get(i).getData().isEmpty()) {
                        textView.setVisibility(GONE);
                    } else {
                        textView.setVisibility(View.VISIBLE);


                        textView.setText(Html.fromHtml("" + (baseActivity.sharedPrefUtils.isEnglishLang() ? columnsData.get(i).getData() : columnsData.get(i).getDataUrdu())));
                        String fieldType = columnsData.get(i).getField_type();
                        final int finalI1 = i;
                        if (fieldType.equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.phone))) {

                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pfaListItem.doPhoneCall(columnsData.get(finalI1).getData());
                                    Log.d("viewCreated", "pfaTableAdapter text Click");
                                }
                            });
                        }


                        if (conducted_inspection) {
                            if (columnsData.get(i).getPrintHtmlStr() != null) {
                                print = columnsData.get(i).getPrintHtmlStr();
                            }

                            if (columnsData.get(i).getShareHtmlStr() != null) {
                                share = columnsData.get(i).getShareHtmlStr();
                            }
                        }


//                            if (columnsData.get(i).getPrintHtmlStr() != null && columnsData.get(i).getShareHtmlStr() != null){
//                                    convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);
//                                    final int finalI3 = i;
//                                    final int finalI5 = i;
//                                    convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
//                                            builder.setTitle("Share And Print");
//
//                                            String[] Items = {baseActivity.getString(R.string.p), baseActivity.getString(R.string.share)};
//                                            builder.setItems(Items, new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    switch (which) {
//                                                        case 0:
//                                                            Intent intent = new Intent(baseActivity, PrinterActivity.class);
//                                                            intent.putExtra("PrintHtml",(columnsData.get(finalI3).getPrintHtmlStr()));
//                                                            baseActivity.startActivity(intent);
//                                                            break;
//                                                        case 1:
//                                                            baseActivity.sharedPrefUtils.shareOnWhatsApp(columnsData.get(finalI5).getShareHtmlStr());
//                                                            break;
//                                                    }
//                                                }
//                                            });
//
//                                            AlertDialog dialog = builder.create();
//                                            dialog.show();
//                                        }
//                                    });
//                                }
//                            else if (columnsData.get(i).getPrintHtmlStr() != null){
//                                    convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);
//                                    final int finalI4 = i;
//                                    convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
//                                            builder.setTitle("Do you want to Print?");
//
//                                            String[] Items = {baseActivity.getString(R.string.p)};
//                                            builder.setItems(Items, new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    switch (which) {
//                                                        case 0:
//                                                            Intent intent = new Intent(baseActivity, PrinterActivity.class);
//                                                            intent.putExtra("PrintHtml",(columnsData.get(finalI4).getPrintHtmlStr()));
//                                                            baseActivity.startActivity(intent);
//                                                            break;
//                                                    }
//                                                }
//                                            });
//
//                                            AlertDialog dialog = builder.create();
//                                            dialog.show();
//                                        }
//                                    });
//                                }
//                            else if (columnsData.get(i).getShareHtmlStr() != null){
//
//                                    convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);
//                                    final int finalI6 = i;
//                                    convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
//                                            builder.setTitle("Do you want to Share?");
//
//                                            String[] Items = {baseActivity.getString(R.string.share)};
//                                            builder.setItems(Items, new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    switch (which) {
//                                                        case 0:
//                                                            baseActivity.sharedPrefUtils.shareOnWhatsApp(columnsData.get(finalI6).getShareHtmlStr());
//                                                            break;
//                                                    }
//                                                }
//                                            });
//
//                                            AlertDialog dialog = builder.create();
//                                            dialog.show();
//                                        }
//                                    });
//                                }
//                            else {
//                                convertView.findViewById(R.id.shareImgBtn).setVisibility(GONE);
//                            }
//                        }


                        if (columnsData.get(i).getField_name() != null && columnsData.get(i).getField_name().equalsIgnoreCase("business_visit")) {
                            if (columnsData.get(i).getAPI_URL() != null && (!columnsData.get(i).getAPI_URL().isEmpty())) {

                                textView.setOnClickListener(v -> baseActivity.httpService.getListsData(columnsData.get(finalI1).getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                                    @Override
                                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                        if (response != null) {
                                            LocalFormDialog localFormDialog = new LocalFormDialog(baseActivity);
                                            localFormDialog.addBusinessVisitDialog(response);
                                        }
                                    }
                                }, true));
                            }
                        } else if (columnsData.get(i).getField_name() != null && columnsData.get(i).getField_name().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.local_add_newUrl))) {
                            if (columnsData.get(i).getLocal_add_newUrl() != null && (!columnsData.get(i).getLocal_add_newUrl().isEmpty())) {

                                final int finalI2 = i;
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("viewCreated", "pfaTableAdapter text Click 1");

                                        final Bundle bundle = new Bundle();
                                        bundle.putString(EXTRA_URL_TO_CALL, columnsData.get(finalI2).getLocal_add_newUrl());

                                        baseActivity.httpService.getListsData(columnsData.get(finalI2).getLocal_add_newUrl(), new HashMap<String, String>(), new HttpResponseCallback() {
                                            @Override
                                            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                                if (response != null)
                                                    bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());

                                                Log.d("viewCreated" , "pfa table adapter new activity 2");
                                                baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, LocalFormsActivity.class, bundle, RC_REFRESH_LIST);
                                            }
                                        }, true);

                                    }
                                });
                            }
                        } else if (columnsData.get(i).getField_name() != null && columnsData.get(i).getField_name().equalsIgnoreCase(String.valueOf(AppUtils.FIELD_TYPE.submit_category_button))) {
                            if (columnsData.get(i).getSubmit_category_button() != null && (!columnsData.get(i).getSubmit_category_button().isEmpty())) {

                                final int finalI2 = i;
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("viewCreated", "pfaTableAdapter text Click 3");

                                        final Bundle bundle = new Bundle();
                                        bundle.putString(EXTRA_URL_TO_CALL, columnsData.get(finalI2).getSubmit_category_button());

                                        baseActivity.httpService.getListsData(columnsData.get(finalI2).getSubmit_category_button(), new HashMap<String, String>(), new HttpResponseCallback() {
                                            @Override
                                            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                                if (response != null)
                                                    bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());

                                                Log.d("viewCreated" , "pfa table adapter new activity 3");
                                                baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, PFAAddNewActivity.class, bundle, RC_REFRESH_LIST);
                                            }
                                        }, true);

                                    }
                                });
                            }
                        }

                    }
                }
            }
        }

//        if (conducted_inspection){
//
//            if (print_data != null){
//
////            if(section_name.equalsIgnoreCase("Sample") || section_name.equalsIgnoreCase("Confiscated Machinery")){
//
//                printB.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(baseActivity, PrinterActivity.class);
//                        intent.putExtra("PrintHtml",print_data);
//                        baseActivity.startActivity(intent);
//                    }
//                });
//            }else {
//                printB.setVisibility(GONE);
//            }
//        }

        if (conducted_inspection) {
            if (print != null && share != null) {
                convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);

                convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                        builder.setTitle("Share And Print");

                        String[] Items = {baseActivity.getString(R.string.p), baseActivity.getString(R.string.share)};
                        builder.setItems(Items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(baseActivity, PrinterActivity.class);
                                        intent.putExtra("PrintHtml", print);
                                        baseActivity.startActivity(intent);
                                        break;
                                    case 1:
                                        baseActivity.sharedPrefUtils.shareOnWhatsApp(share);
                                        break;
                                }
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            } else if (print != null) {
                convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);

                convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(baseActivity, PrinterActivity.class);
                        intent.putExtra("PrintHtml", print);
                        baseActivity.startActivity(intent);

                    }
                });
            } else if (share != null) {
                convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);

                convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baseActivity.sharedPrefUtils.shareOnWhatsApp(share);
                    }
                });
            } else {
                convertView.findViewById(R.id.shareImgBtn).setVisibility(GONE);
            }
        }

        if (showDeseize) {
            convertView.findViewById(R.id.deseizeBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whichItemClicked.whichItemClicked("" + position);
                }
            });
        }

        if (suggestions.get(position).get(0).getDelete_url() != null && (!suggestions.get(position).get(0).getDelete_url().isEmpty())) {
            convertView.findViewById(R.id.deleteImgBtn).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.deleteImgBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    baseActivity.sharedPrefUtils.showTwoBtnsMsgDialog(baseActivity.getString(R.string.delete_message), new SendMessageCallback() {
                        @Override
                        public void sendMsg(String message) {
                            if (message.equalsIgnoreCase(CANCEL))
                                return;
                            whichItemClicked.deleteRecordAPICall(suggestions.get(position).get(0).getDelete_url(), position);
                        }
                    });
                }
            });
        }
        if (showDeleteIcon) {
            convertView.findViewById(R.id.deleteImgBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    whichItemClicked.whichItemClicked("" + position);
                }
            });
        } else {
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("viewCreated", "pfaTableAdapter text Click2 = " + conducted_inspection);
                    if (isClicked) {
                        return;
                    }

                    isClicked = true;

                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isClicked = false;
                        }
                    }, 100);

                    if (suggestions.get(position).get(0).getField_link() != null) {
                        if (baseActivity.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + suggestions.get(position).get(0).getField_link()));
                            baseActivity.startActivity(intent);
                        } else {
                            baseActivity.sharedPrefUtils.showMsgDialog("Permission to make phone call denied" + "\n" + "phone # " + suggestions.get(position).get(0).getField_link(), null);
                        }

                    } else {

                        if (suggestions.get(position).get(0).getLocal_add_newUrl() == null || suggestions.get(position).get(0).getLocal_add_newUrl().isEmpty()) {

                            if (suggestions.get(position).get(0).getAPI_URL() == null || suggestions.get(position).get(0).getAPI_URL().isEmpty()) {
                                return;
                            }

                            final Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_URL_TO_CALL, suggestions.get(position).get(0).getAPI_URL());
                            Log.d("BusinessDetailsMenu", "EXTRA_URL_TO_CALL = " + suggestions.get(position).get(0).getAPI_URL());
                            bundle.putString(EXTRA_ACTIVITY_TITLE, baseActivity.sharedPrefUtils.isEnglishLang() ? suggestions.get(position).get(0).getValue() : suggestions.get(position).get(0).getValueUrdu());
                            baseActivity.httpService.getListsData(suggestions.get(position).get(0).getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                                @Override
                                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {

                                    if (response != null)
                                        bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());

                                    Log.d("BusinessDetailsMenu", "after getting response = " );
                                    baseActivity.sharedPrefUtils.startNewActivity(PFADetailActivity.class, bundle, false);
                                }
                            }, true);

                        } else {
                            final Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_URL_TO_CALL, suggestions.get(position).get(0).getLocal_add_newUrl());
                            bundle.putString(EXTRA_DOWNLOAD_URL, suggestions.get(position).get(0).getDownload_url());
                            baseActivity.httpService.getListsData(suggestions.get(position).get(0).getLocal_add_newUrl(), new HashMap<String, String>(), new HttpResponseCallback() {
                                @Override
                                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                    if (response != null) {
                                        Log.d("viewCreated" , "pfa table adapter new activity");
                                        bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                        baseActivity.sharedPrefUtils.startActivityForResult(baseActivity, LocalFormsActivity.class, bundle, RC_REFRESH_LIST);
                                    } else {
                                        baseActivity.sharedPrefUtils.showMsgDialog(baseActivity.getResources().getString(R.string.server_error), null);
                                    }
                                }
                            }, true);
                        }

                    }
                }
            };
            if (!suggestions.get(position).get(0).isNotClickable())
                convertView.setOnClickListener(onClickListener);
        }

        if (suggestions.get(position).get(0).getDownload_url() != null && (!suggestions.get(position).get(0).getDownload_url().isEmpty()) && suggestions.get(position).get(0).isShow_download_btn()) {
            convertView.findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.downloadImgBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    baseActivity.sharedPrefUtils.showTwoBtnsMsgDialog(baseActivity.getResources().getString(R.string.saveInspectionOfflineMsg), new SendMessageCallback() {
                        @Override
                        public void sendMsg(String message) {
                            if (message.equalsIgnoreCase(CANCEL))
                                return;

                            whichItemClicked.downloadInspection(suggestions.get(position).get(0).getDownload_url(), position);
                        }
                    });
                }
            });
        } else {
            convertView.findViewById(R.id.downloadImgBtn).setVisibility(GONE);
        }


        //////// share icon show start
//        if (suggestions.get(position).get(0).getShareHtmlStr() != null && (!suggestions.get(position).get(0).getShareHtmlStr().isEmpty())) {
//***************
//        if (suggestions.get(position).get(0).getShareHtmlStr() != null && (suggestions.get(position).get(0).getPrintHtmlStr() != null)) {
//            convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);
//            convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
//                    builder.setTitle("Share And Print");
//
//                    // add a list
//                    String[] Items = {baseActivity.getString(R.string.p), baseActivity.getString(R.string.share)};
//                    builder.setItems(Items, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            switch (which) {
//                                case 0:
////                                    baseActivity.Connect(suggestions.get(position).get(0).getReceipt_logo(),suggestions.get(position).get(0).getBarcode_url(),suggestions.get(position).get(0).getPrintHtmlStr());
////                                    baseActivity.Connect(suggestions.get(position).get(0).getPrintHtmlStr());
//
//                                    Intent intent = new Intent(baseActivity, PrinterActivity.class);
//                                    intent.putExtra("PrintHtml", (suggestions.get(position).get(0).getPrintHtmlStr()));
//                                    baseActivity.startActivity(intent);
//
//
//                                    break;
//                                case 1:
//
//                                    baseActivity.sharedPrefUtils.shareOnWhatsApp(suggestions.get(position).get(0).getShareHtmlStr());
//                                    break;
//                            }
//                        }
//                    });
//
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
////                    baseActivity.sharedPrefUtils.shareOnWhatsApp(suggestions.get(position).get(0).getShareHtmlStr());
//                }
//            });
//        } else {
////            convertView.findViewById(R.id.shareImgBtn).setVisibility(GONE);
//        }


        ///////  share icons show end


        ///////////////////hhh
        if (suggestions.get(position).get(0).getShareHtmlStr() != null && suggestions.get(position).get(0).getPrintHtmlStr() != null) {
            convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);

            convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
                    builder.setTitle("Share And Print");

                    String[] Items = {baseActivity.getString(R.string.p), baseActivity.getString(R.string.share)};
                    builder.setItems(Items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(baseActivity, PrinterActivity.class);
                                    intent.putExtra("PrintHtml", (suggestions.get(position).get(0).getPrintHtmlStr()));
                                    baseActivity.startActivity(intent);
                                    break;
                                case 1:
                                    baseActivity.sharedPrefUtils.shareOnWhatsApp(suggestions.get(position).get(0).getShareHtmlStr());
                                    break;
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        } else if (suggestions.get(position).get(0).getPrintHtmlStr() != null) {
            convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);

            convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(baseActivity, PrinterActivity.class);
                    intent.putExtra("PrintHtml", (suggestions.get(position).get(0).getPrintHtmlStr()));
                    baseActivity.startActivity(intent);

                }
            });
        } else if (suggestions.get(position).get(0).getShareHtmlStr() != null) {
            convertView.findViewById(R.id.shareImgBtn).setVisibility(View.VISIBLE);

            convertView.findViewById(R.id.shareImgBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    baseActivity.sharedPrefUtils.shareOnWhatsApp(suggestions.get(position).get(0).getShareHtmlStr());
                }
            });
        } else {
            convertView.findViewById(R.id.shareImgBtn).setVisibility(GONE);
        }
///////////////////jjj
        if (suggestions.get(position).get(0).getLicense_number() != null && (!suggestions.get(position).get(0).getLicense_number().isEmpty())) {
            convertView.findViewById(R.id.licenseVerified).setVisibility(View.VISIBLE);
        } else {
            convertView.findViewById(R.id.licenseVerified).setVisibility(GONE);
        }

        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void methodCall() {

    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    /**
     * Our Custom Filter Class.
     */
    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            suggestions.clear();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() < 1) {
                suggestions.addAll(originalList);

                results.values = suggestions;
                results.count = suggestions.size();
                return results;
            }

            if (originalList != null) { // Check if the Original List and Constraint aren't null.
                for (int i = 0; i < originalList.size(); i++) {
                    List<PFATableInfo> columnsData = originalList.get(i);

                    boolean isValueExist = false;
                    for (int j = 0; j < columnsData.size(); j++) {
                        if (!columnsData.get(j).getField_type().equals("imageView")) {
                            if (columnsData.get(j).getData().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                // Compare item in original list if it contains constraints.
                                isValueExist = true;
                            }
                        }
                    }

                    if (isValueExist) {
                        suggestions.add(originalList.get(i)); // If TRUE add item in Suggestions.
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }


    }


}
