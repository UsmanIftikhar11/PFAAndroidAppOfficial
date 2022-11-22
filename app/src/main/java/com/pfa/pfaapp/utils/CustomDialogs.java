package com.pfa.pfaapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.LocalFormsActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.adapters.DropdownAdapter;
import com.pfa.pfaapp.customviews.custominputlayout.CustomTextInputLayout;
import com.pfa.pfaapp.httputils.HttpService;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.models.SearchBizInspInfo;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;
import static com.pfa.pfaapp.utils.AppConst.INSPECTION_ID;
import static com.pfa.pfaapp.utils.AppConst.SP_APP_LANG;
import static com.pfa.pfaapp.utils.AppConst.SP_LOGIN_TYPE;

/**
 * CustomDialogs
 */
public class CustomDialogs {

    private static Dialog alertDialog;
    protected Context mContext;

    CustomDialogs(Context mContext) {
        this.mContext = mContext;
    }

    public void applyFont(View view, AppUtils.FONTS fonts) {

        boolean isEnglish = isEnglishLang();
        Typeface typeface;
        switch (fonts) {
            case HelveticaNeueMedium:
                typeface = Typeface.createFromAsset(mContext.getAssets(), isEnglish ? "fonts/HelveticaNeueMedium.ttf" : "fonts/JameelNooriNastaleeq.ttf");
                break;
            case HelveticaNeueBold:
                typeface = Typeface.createFromAsset(mContext.getAssets(), isEnglish ? "fonts/helveticaNeueBold.ttf" : "fonts/JameelNooriNastaleeqKasheeda.ttf");
                break;
            default:
                typeface = Typeface.createFromAsset(mContext.getAssets(), isEnglish ? "fonts/HelveticaNeue.ttf" : "fonts/JameelNooriNastaleeq.ttf");
                break;
        }

        if (view != null)
            setTypeface(view, typeface);
    }

    private void setTypeface(View view, Typeface typeface) {
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(typeface);
        } else if (view instanceof CustomTextInputLayout) {
            ((CustomTextInputLayout) view).setTypeface(typeface);
        }
    }

    public void printLog(String tag, String message) {
        Log.e(tag, message);
    }

    public void printLogD(String tag, String message) {
        Log.d(tag, message);
    }

    public void printStackTrace(Exception e) {
        e.printStackTrace();
    }

    /*Show progress dialog
     * Only one alert dialog can be shown at a time.
     * If alertDialog already showing, it will return back without creating new alertDialog
     * */
    public void showProgressDialog(boolean cancelable) {
        if ((alertDialog != null) && (alertDialog.isShowing())) return;

        if (((Activity) mContext).isFinishing())
            return;
        alertDialog = new Dialog(mContext, R.style.CustomDialog);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LayoutInflater li = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.progress_dialog_layout, null);

        alertDialog.setContentView(view);
        alertDialog.setCancelable(cancelable);

        ImageView imageView = view.findViewById(R.id.animationView);
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(mContext).load(R.raw.loader3a).into(imageView);

        ImageView animationView1 = view.findViewById(R.id.animationView1);
//        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(mContext).load(R.raw.loader3).into(animationView1);

       try {
           if (mContext != null && !((Activity) mContext).isDestroyed()) {
               alertDialog.show();
           }
       }catch (Exception e)
       {
           printStackTrace(e);
       }
    }

    /*
     * Hide the progress AlertDialog if it is showing otherwise do nothing*/
    public void hideProgressDialog() {
        try {
            if (!((Activity) mContext).isFinishing()) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
            }

        } catch (Exception e) {
            alertDialog =null;
            e.printStackTrace();
        }
    }

    /*show toast message for short time*/
    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void showMsgDialog(String message, final SendMessageCallback callback) {
        showMsgDialog(message, mContext.getString(R.string.app_name), callback);
    }

    /**
     * Generalized message dialog
     * Title of dialog is the application name set by default
     * Note: Only one alert dialog is shown at a time. If alertDialog is already showing then it returns otherwise creates new dialog
     *
     * @param message  {@link String}  message to be shown
     * @param title    {@link String}
     * @param callback {@link SendMessageCallback} it is an interface that is used to send call back to the callee method
     */
    public void showMsgDialog(String message, String title, final SendMessageCallback callback) {
        if ((alertDialog != null) && (alertDialog.isShowing())) {
            hideProgressDialog();
//            return;
        }

        alertDialog = new Dialog(mContext);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater li = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.dialog_with_one_option, null);

        TextView oneBtnsDialogMsg = view.findViewById(R.id.oneBtnsDialogMsg);
        oneBtnsDialogMsg.setText(message);

        TextView oneBtnsDialogTitle = view.findViewById(R.id.oneBtnsDialogTitle);
        oneBtnsDialogTitle.setText(title);

        Button oneBtnDialogOk = view.findViewById(R.id.oneBtnDialogOk);
        oneBtnDialogOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null)
                    alertDialog.dismiss();
                alertDialog = null;

                if (callback != null) {
                    callback.sendMsg("");
                }
            }
        });

        applyFont(oneBtnsDialogTitle, AppUtils.FONTS.HelveticaNeueBold);
        applyFont(oneBtnsDialogMsg, AppUtils.FONTS.HelveticaNeueMedium);
        applyFont(oneBtnDialogOk, AppUtils.FONTS.HelveticaNeueMedium);

        alertDialog.setCancelable(false);
        alertDialog.setContentView(view);

        try {
            if (mContext!=null && (mContext instanceof Activity) &&  (!((Activity) mContext).isFinishing())) {
                if (alertDialog != null)
                    alertDialog.show();
            }
        } catch (Exception e) {
            printStackTrace(e);
        }
    }

    /**
     * if you want to exit from application, then this method is called:
     * <p>
     * What it does:
     * If any alert dialog is showing, it returns otherwise it shows options dialogs and asks whether to exit or not?
     * If yes: It finishes the activity and exits from app otherwise just cancel the message dialog
     */
    private static Dialog exitDialog;

    public void showExitDialog() {
        if ((exitDialog != null) && (exitDialog.isShowing())) {
            return;
        }
        exitDialog = new Dialog(mContext);
        exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        exitDialog.getWindow().setBackgroundDrawable(null);
        LayoutInflater li = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.two_buttons_custom_dialog, null);

        TextView dialogmessage = view.findViewById(R.id.dialogmessage);
        dialogmessage.setText(R.string.exitmsg);

        Button yesbtn = view.findViewById(R.id.yesbtn);

        yesbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (exitDialog != null)
                    exitDialog.dismiss();

                BaseActivity baseActivity = (BaseActivity) mContext;
                baseActivity.stopLocation();
                baseActivity.finish();
                baseActivity.finishAffinity();
//                baseActivity.onBackPressed();
                exitDialog = null;

            }
        });

        Button noBtn = view.findViewById(R.id.noBtn);

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exitDialog != null)
                    exitDialog.dismiss();
                exitDialog = null;
            }
        });

        applyFont(view.findViewById(R.id.dialogtitle), AppUtils.FONTS.HelveticaNeueBold);
        applyFont(dialogmessage, AppUtils.FONTS.HelveticaNeue);
        applyFont(yesbtn, AppUtils.FONTS.HelveticaNeueMedium);
        applyFont(noBtn, AppUtils.FONTS.HelveticaNeueMedium);

        exitDialog.setContentView(view);

        if (!((Activity) mContext).isFinishing()) {
            Window window = exitDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            exitDialog.show();
        }
    }

    void showSelectPictureDialog(final SendMessageCallback sendMsgCallback, boolean showVideoBtns, final boolean isMultiple) {
        if ((alertDialog != null) && (alertDialog.isShowing()))
            return;
        alertDialog = new Dialog(mContext);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater li = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.photo_dialog_layout, null);

        Button cancelBtn = view.findViewById(R.id.cancelBtn);

        Button takePhotoFromCameraBtn = view.findViewById(R.id.takePhotoFromCameraBtn);
        takePhotoFromCameraBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMsgCallback.sendMsg(String.valueOf(AppConst.CAPTURE_PHOTO));
                if (alertDialog != null)
                    alertDialog.dismiss();

            }
        });
        Button chooseFromGalleryBtn = view.findViewById(R.id.chooseFromGalleryBtn);
        chooseFromGalleryBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendMsgCallback.sendMsg("" + AppConst.CHOOSE_FROM_GALLERY);
                alertDialog.dismiss();
                alertDialog = null;
            }
        });
        Button recordVideoBtn = view.findViewById(R.id.recordVideoBtn);
        if (showVideoBtns) {
            recordVideoBtn.setVisibility(View.VISIBLE);
            recordVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMsgCallback.sendMsg("" + AppConst.RECORD_VIDEO);
                    if (alertDialog != null)
                        alertDialog.dismiss();
                    alertDialog = null;
                }
            });
        }

        Button multiImagesBtn = view.findViewById(R.id.multiImagesBtn);

        if (!isMultiple) {
            multiImagesBtn.setVisibility(View.GONE);
        } else {
            multiImagesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMsgCallback.sendMsg("" + AppConst.MULTIPLE_IMAGES);
                    if (alertDialog != null)
                        alertDialog.dismiss();
                    alertDialog = null;
                }
            });
        }

        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null)
                    alertDialog.dismiss();
                alertDialog = null;
            }
        });
        applyFont(view.findViewById(R.id.ttleTV), AppUtils.FONTS.HelveticaNeueMedium);
        applyFont(takePhotoFromCameraBtn, AppUtils.FONTS.HelveticaNeue);
        applyFont(cancelBtn, AppUtils.FONTS.HelveticaNeue);
        applyFont(chooseFromGalleryBtn, AppUtils.FONTS.HelveticaNeue);
        applyFont(multiImagesBtn, AppUtils.FONTS.HelveticaNeue);
        applyFont(recordVideoBtn, AppUtils.FONTS.HelveticaNeue);

        alertDialog.setCancelable(false);
        alertDialog.setContentView(view);
        if (!((Activity) mContext).isFinishing()) {
            Window window = alertDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            alertDialog.show();
        } else {
            printLog("Activity Finishing", "CustomDialogs.java Activity Finishing");
        }
    }

    public void checkPermission(String[] permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(mContext, String.valueOf(permission)) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[] {String.valueOf(permission)}, requestCode);
        }
        else {
            Toast.makeText(mContext, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }



    public void showTwoBtnsMsgDialog(String message, final SendMessageCallback callback) {
        if ((alertDialog != null) && (alertDialog.isShowing())) {
            if (alertDialog != null) {
                alertDialog.dismiss();
                alertDialog.cancel();
            }

        }
        alertDialog = new Dialog(mContext);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.getWindow().setBackgroundDrawable(null);
        LayoutInflater li = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.two_buttons_custom_dialog, null);

        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView dialogmessage = view.findViewById(R.id.dialogmessage);
        dialogmessage.setText(Html.fromHtml(message));

        Button yesbtn = view.findViewById(R.id.yesbtn);
        yesbtn.setText(R.string.ok);

        yesbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null)
                    alertDialog.dismiss();
                alertDialog = null;

                if (callback != null) {
                    callback.sendMsg("");
                }
            }
        });

        Button noBtn = view.findViewById(R.id.noBtn);
        noBtn.setText(R.string.cancel);
        noBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog != null)
                    alertDialog.dismiss();
                alertDialog = null;
                if (callback != null) {
                    callback.sendMsg(AppConst.CANCEL);
                }
            }
        });

        applyFont(view.findViewById(R.id.dialogtitle), AppUtils.FONTS.HelveticaNeueBold);
        applyFont(dialogmessage, AppUtils.FONTS.HelveticaNeue);
        applyFont(yesbtn, AppUtils.FONTS.HelveticaNeueMedium);
        applyFont(noBtn, AppUtils.FONTS.HelveticaNeueMedium);

        alertDialog.setContentView(view);

        if (!((Activity) mContext).isFinishing()) {
            Window window = alertDialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if (alertDialog != null)
                alertDialog.show();
        }
    }

    void showThreeBtnsMsgDialog(String message, final SendMessageCallback callback, final String saveText, boolean hideDraftBtn) {

        final Dialog[] alertDialog = {new Dialog(mContext)};
        alertDialog[0].requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog[0].getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog[0].getWindow().setBackgroundDrawable(null);
        LayoutInflater li = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.three_buttons_custom_dialog, null);

        TextView dialogmessage = view.findViewById(R.id.dialogmessage);
        dialogmessage.setText(message);

        final Button eixtBTn = view.findViewById(R.id.eixtBTn);

        if (saveText != null)
            eixtBTn.setText(saveText);
        eixtBTn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog[0] != null)
                    alertDialog[0].dismiss();
                alertDialog[0] = null;
                if (saveText != null) {
                    if (callback != null) {
                        callback.sendMsg(saveText);
                    }

                } else if (callback != null) {
                    callback.sendMsg(String.valueOf(AppUtils.INSPECTION_ACTION.Exit));
                }
            }
        });

        Button saveInDraftsBtn = view.findViewById(R.id.saveInDraftsBtn);
        saveInDraftsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog[0] != null)
                    alertDialog[0].dismiss();
                alertDialog[0] = null;

                if (callback != null) {
                    callback.sendMsg(String.valueOf(AppUtils.INSPECTION_ACTION.Draft));
                }
            }
        });

        alertDialog[0].setContentView(view);

        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (alertDialog[0] != null)
                    alertDialog[0].dismiss();
                alertDialog[0] = null;
            }
        });

        alertDialog[0].setContentView(view);

        applyFont(view.findViewById(R.id.dialogtitle), AppUtils.FONTS.HelveticaNeueMedium);
        applyFont(dialogmessage, AppUtils.FONTS.HelveticaNeue);
        applyFont(saveInDraftsBtn, AppUtils.FONTS.HelveticaNeueMedium);
        applyFont(cancelBtn, AppUtils.FONTS.HelveticaNeueMedium);
        applyFont(eixtBTn, AppUtils.FONTS.HelveticaNeueMedium);

        if (hideDraftBtn)
            saveInDraftsBtn.setVisibility(View.GONE);

        if (!((Activity) mContext).isFinishing()) {
            Window window = alertDialog[0].getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            alertDialog[0].show();
        }
    }

    protected void showInspectionList(JSONObject SEARCH_BIZ_JSON_OBJ) {
        if (SEARCH_BIZ_JSON_OBJ.has("inspections")) {
            Type type = new TypeToken<List<SearchBizInspInfo>>() {
            }.getType();

            final List<SearchBizInspInfo> insInfo = new GsonBuilder().create().fromJson(SEARCH_BIZ_JSON_OBJ.optJSONArray("inspections").toString(), type);

            if (insInfo != null && insInfo.size() > 0) {

                List<String> inspIds = new ArrayList<>();
                for (int i = 0; i < insInfo.size(); i++) {
                    inspIds.add((i + 1) + ". " + insInfo.get(i).getInspection_id() + " / " + insInfo.get(i).getStart_date() + " / " + insInfo.get(i).getInspection_type());
                }

                final Dialog[] alertDialog = {new Dialog(mContext)};
                alertDialog[0].requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog[0].getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                LayoutInflater li = LayoutInflater.from(mContext);
                @SuppressLint("InflateParams") View view = li.inflate(R.layout.show_insp_list, null);

                TextView insTtlTV = view.findViewById(R.id.insTtlTV);
                ListView inspLV = view.findViewById(R.id.inspLV);

                DropdownAdapter dropdownAdapter = new DropdownAdapter(mContext, inspIds);
                inspLV.setAdapter(dropdownAdapter);

                applyFont(insTtlTV, AppUtils.FONTS.HelveticaNeueMedium);

                inspLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (alertDialog[0] != null)
                            alertDialog[0].dismiss();
                        alertDialog[0] = null;

                        if (insInfo.get(position).getAPI_URL() != null && (!insInfo.get(position).getAPI_URL().isEmpty())) {
                            final Bundle bundle = new Bundle();
                            bundle.putString(EXTRA_URL_TO_CALL, insInfo.get(position).getAPI_URL());

                            final HttpService httpService = new HttpService(mContext);

                            httpService.getListsData(insInfo.get(position).getAPI_URL(), new HashMap<String, String>(), new HttpResponseCallback() {
                                @Override
                                public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
                                    if (response != null)
                                        bundle.putString(EXTRA_JSON_STR_RESPONSE, response.toString());
                                    httpService.startNewActivity(LocalFormsActivity.class, bundle, true);
                                }
                            }, true);
                        } else {
                            INSPECTION_ID = "" + insInfo.get(position).getInspection_id();
                        }
                    }
                });
                alertDialog[0].setCancelable(false);
                alertDialog[0].setContentView(view);

                if (!((Activity) mContext).isFinishing()) {
                    Window window = alertDialog[0].getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    alertDialog[0].show();
                }
            } else {
                INSPECTION_ID = null;
            }
        }
    }

    public boolean isEnglishLang() {

        SharedPrefUtils sharedPrefUtils = new SharedPrefUtils(mContext);
        boolean isFBO = sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, "") == null || String.valueOf(AppUtils.USER_LOGIN_TYPE.fbo).equalsIgnoreCase(sharedPrefUtils.getSharedPrefValue(SP_LOGIN_TYPE, ""));
        String lang = sharedPrefUtils.getSharedPrefValue(SP_APP_LANG, "");
        if (lang == null)
            lang = String.valueOf(AppUtils.APP_LANGUAGE.en);

        return !isFBO || !lang.equalsIgnoreCase(String.valueOf(AppUtils.APP_LANGUAGE.ur));
    }


    private static Dialog invalidUserDialg;

    protected void showInvalidUserDialog(String message, final SendMessageCallback callback) {


        if (invalidUserDialg != null && invalidUserDialg.isShowing())
            return;

        invalidUserDialg = new Dialog(mContext);
        invalidUserDialg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        invalidUserDialg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater li = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View view = li.inflate(R.layout.dialog_with_one_option, null);

        TextView oneBtnsDialogMsg = view.findViewById(R.id.oneBtnsDialogMsg);
        oneBtnsDialogMsg.setText(message);

        TextView oneBtnsDialogTitle = view.findViewById(R.id.oneBtnsDialogTitle);
        oneBtnsDialogTitle.setText(mContext.getResources().getString(R.string.app_name));

        Button oneBtnDialogOk = view.findViewById(R.id.oneBtnDialogOk);
        oneBtnDialogOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (invalidUserDialg != null)
                    invalidUserDialg.dismiss();
                invalidUserDialg = null;

                if (callback != null) {
                    callback.sendMsg("");
                }
            }
        });

        applyFont(oneBtnsDialogTitle, AppUtils.FONTS.HelveticaNeueBold);
        applyFont(oneBtnsDialogMsg, AppUtils.FONTS.HelveticaNeueMedium);
        applyFont(oneBtnDialogOk, AppUtils.FONTS.HelveticaNeueMedium);

        invalidUserDialg.setCancelable(false);
        invalidUserDialg.setContentView(view);

        if (!((Activity) mContext).isFinishing()) {
            if (invalidUserDialg != null)
                invalidUserDialg.show();
        }
    }
}
