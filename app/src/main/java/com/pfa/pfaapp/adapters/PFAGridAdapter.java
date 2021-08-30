/*
 * Copyright (c) 2018. All Rights Reserved by Punjab Food Authority
 */

package com.pfa.pfaapp.adapters;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.FBOMainGridActivity;
import com.pfa.pfaapp.LoginActivity;
import com.pfa.pfaapp.MapsActivity;
import com.pfa.pfaapp.PFAAddNewActivity;
import com.pfa.pfaapp.PFADetailActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.SignupActivity;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.models.PFAMenuInfo;
import com.pfa.pfaapp.utils.AppUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_ACTIVITY_TITLE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_PFA_MENU_ITEM;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_SINGLE_TOP;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_STARTING_ACTIVITY;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.SP_IS_LOGED_IN;
import static com.pfa.pfaapp.utils.AppConst.SP_MAIN_MENU;
import static com.pfa.pfaapp.utils.AppConst.SP_STAFF_ID;

public class PFAGridAdapter extends BaseAdapter {
    private BaseActivity baseActivity;
    private List<PFAMenuInfo> pfaMenuInfos;
    private boolean isClicked = false;
    private boolean isEnglish;

    public PFAGridAdapter(BaseActivity baseActivity, List<PFAMenuInfo> pfaMenuInfos) {
        this.baseActivity = baseActivity;
        this.pfaMenuInfos = pfaMenuInfos;

        isEnglish = baseActivity.sharedPrefUtils.isEnglishLang();
    }

    @Override
    public int getCount() {
        return pfaMenuInfos.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(baseActivity);

        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fbo_grid_item, parent, false);
            holder = new ViewHolder();
            holder.menuCNIV = convertView.findViewById(R.id.menuCNIV);
            holder.fboMenuNameTV = convertView.findViewById(R.id.fboMenuNameTV);
            holder.fbo_grid_ll = convertView.findViewById(R.id.fbo_grid_ll);

            baseActivity.sharedPrefUtils.applyFont(holder.fboMenuNameTV, AppUtils.FONTS.HelveticaNeueMedium);

            convertView.setTag(holder);
            convertView.setBackground(baseActivity.getResources().getDrawable(R.drawable.list_item_selector));
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isEnglish) {
            holder.fboMenuNameTV.setText(pfaMenuInfos.get(position).getMenuItemName());
        } else {
            holder.fboMenuNameTV.setText(pfaMenuInfos.get(position).getMenuItemNameUrdu());
        }

        holder.menuCNIV.setImageURI(pfaMenuInfos.get(position).getMenuItemImg());

        convertView.setBackgroundColor(baseActivity.sharedPrefUtils.colorFromHexDecimal(pfaMenuInfos.get(position).getBg_color()));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                switch (pfaMenuInfos.get(position).getMenuType()) {
                    case "login":
                    case "user_login":
                    case "signup":
                        signupLoginActivity(position, pfaMenuInfos.get(position).getMenuType());
                        break;

                    case "logout":
//                        baseActivity.sharedPrefUtils.logoutFromApp(baseActivity.httpService);
                        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
//                builder.setTitle("Log out");

                        String[] options = {"Log Out","Log Out from All Devices"};
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        if (baseActivity.sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
                                            baseActivity.sharedPrefUtils.logoutFromApp(baseActivity.httpService);
                                        } else {
                                            baseActivity.sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
                                        }
                                        break;
                                    case 1:

                                        if (baseActivity.sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
                                            baseActivity.sharedPrefUtils.logoutFromAllDevices(baseActivity.httpService);
                                        } else {
                                            baseActivity.sharedPrefUtils.startNewActivity(LoginActivity.class, null, false);
                                        }
                                        break;
                                }
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;

                    case "form":

                        if (pfaMenuInfos.get(position) != null && pfaMenuInfos.get(position).getAPI_URL() != null && (!pfaMenuInfos.get(position).getAPI_URL().isEmpty())) {

                            String userId = "";
                            if (baseActivity.sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") != null) {
                                userId = baseActivity.sharedPrefUtils.getSharedPrefValue(SP_STAFF_ID, "");
                                userId = "/" + userId;
                            }

                            baseActivity.httpService.getListsData(pfaMenuInfos.get(position).getAPI_URL() + userId, new HashMap<String, String>(), new HttpResponseCallback() {
                                @Override
                                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                    Bundle bundle = new Bundle();

                                    if (baseActivity.sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") == null) {
                                        baseActivity.sharedPrefUtils.removeSharedPrefValue(SP_MAIN_MENU);
                                    }

                                    bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                    baseActivity.sharedPrefUtils.startNewActivity(PFAAddNewActivity.class, bundle, false);

                                }
                            }, true);
                        }

                        break;

                    case "map_list":
                        if (pfaMenuInfos.get(position).getAPI_URL() == null || pfaMenuInfos.get(position).getAPI_URL().isEmpty()) {
                            return;
                        }

                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_URL_TO_CALL, pfaMenuInfos.get(position).getAPI_URL());
                        bundle.putString(EXTRA_ACTIVITY_TITLE, isEnglish ? pfaMenuInfos.get(position).getMenuItemName() : pfaMenuInfos.get(position).getMenuItemNameUrdu());

                        baseActivity.sharedPrefUtils.startNewActivity(MapsActivity.class, bundle, false);
                        break;

                    case "menu":  // for form
                        final Bundle formBundle = new Bundle();
                        formBundle.putString(EXTRA_URL_TO_CALL, pfaMenuInfos.get(position).getAPI_URL());
                        formBundle.putString(EXTRA_ACTIVITY_TITLE, isEnglish ? pfaMenuInfos.get(position).getMenuItemName() : pfaMenuInfos.get(position).getMenuItemNameUrdu());

                        baseActivity.httpService.getListsData(pfaMenuInfos.get(position).getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                            @Override
                            public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                formBundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                baseActivity.sharedPrefUtils.startNewActivity(PFADetailActivity.class, formBundle, false);

                            }
                        }, true);

                        break;
                    case "list":
                        if (pfaMenuInfos.get(position).getAPI_URL() == null || pfaMenuInfos.get(position).getAPI_URL().isEmpty()) {
                            return;
                        }

                        Bundle listBundle = new Bundle();
                        listBundle.putSerializable(EXTRA_PFA_MENU_ITEM, pfaMenuInfos.get(position));
                        if (isEnglish) {
                            listBundle.putString(EXTRA_ACTIVITY_TITLE, pfaMenuInfos.get(position).getMenuItemName());
                        } else {
                            listBundle.putString(EXTRA_ACTIVITY_TITLE, pfaMenuInfos.get(position).getMenuItemNameUrdu());
                        }
                        baseActivity.sharedPrefUtils.startNewActivity(MapsActivity.class, listBundle, false);
                        break;
                    case "webView":
//                        baseActivity.sharedPrefUtils.startHomeActivity(WebAppActivity.class, null);
                        break;
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView menuCNIV;
        TextView fboMenuNameTV;
        LinearLayout fbo_grid_ll;
    }

    private void signupLoginActivity(int position, String menuType) {
        if (baseActivity.sharedPrefUtils.getSharedPrefValue(SP_IS_LOGED_IN, "") == null) {

            final Bundle bundle = new Bundle();
            bundle.putSerializable(EXTRA_PFA_MENU_ITEM, pfaMenuInfos.get(position));
            bundle.putSerializable(EXTRA_STARTING_ACTIVITY, "" + (FBOMainGridActivity.class.getName()));
            bundle.putBoolean(EXTRA_SINGLE_TOP, true);

            if (menuType.equalsIgnoreCase(String.valueOf(AppUtils.MENU_TYPE.login)) || menuType.equalsIgnoreCase(String.valueOf(AppUtils.MENU_TYPE.user_login))) {

                baseActivity.httpService.getLoginSettings(new HttpResponseCallback() {
                    @Override
                    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                        if (response != null && response.optBoolean("status")) {
                            bundle.putString("type", response.optString("type"));
                            baseActivity.sharedPrefUtils.startNewActivity(LoginActivity.class, bundle, false);
                        } else {
                            assert response != null;
                            baseActivity.sharedPrefUtils.showMsgDialog("" + response.optString("message_code"), null);
                        }
                    }
                });

            } else {
                baseActivity.sharedPrefUtils.startNewActivity(SignupActivity.class, bundle, false);
            }
        }
    }
}
