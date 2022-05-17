package com.pfa.pfaapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.pfa.pfaapp.BTDeviceList;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.customviews.PFAEditText;
import com.pfa.pfaapp.fragments.MenuMapFragment;
import com.pfa.pfaapp.interfaces.SendMessageCallback;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pfa.pfaapp.utils.AppConst.CANCEL;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_SINGLE_TOP;

/**
 * AppUtils->CustomDialogs
 */
public class AppUtils extends CustomDialogs {




    public enum FONTS {HelveticaNeue, HelveticaNeueMedium, HelveticaNeueBold}

    public enum FIELD_TYPE {mediaFormField, get_code_button, text, searchkeytext, textarea, numeric, cnic, phone, email, dropdown, radiogroup, checkbox, label, date, imageView, button, autoSearch, location_fields, abc, local_add_newUrl, local_add_newUrl1 , submit_category_button}

    public enum MENU_TYPE {form, list, menu, googlemap, profile, search, dashboard, grid, draft, login, user_login, logout}

    public enum FONT_SIZE {s, m, l, xl, xxl}

    public enum FONT_STYLE {bold, medium, normal}

    public enum DIRECTION {left, right, clearfix}  // direction is for imageview (whether to show imageview on left or right of list item

    public enum LOGIN_SETTING_TYPE {phone, pin}

    public enum USER_LOGIN_TYPE {fbo, staff, client,mto}

    public enum IMAGE_SHAPE {circle}

    public enum INSPECTION_ACTION {Complete, Draft, Exit, Cancel}

    public enum APP_LANGUAGE {en, ur}

    public AppUtils(Context mContext) {
        super(mContext);
    }

    public static int compareInts(int x, int y) {
        return (x >= y) ? ((x == y) ? 0 : 1) : -1;
    }


    public void applyStyle(String fontStyle, String fontSize, String fontColor, TextView subviewTV) {

        boolean english = isEnglishLang();
        if (fontSize != null) {
            if (fontSize.equalsIgnoreCase(String.valueOf(FONT_SIZE.xxl))) {
                subviewTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, english ? mContext.getResources().getDimension(R.dimen.sp_20) : mContext.getResources().getDimension(R.dimen.sp_20_2));
            } else if (fontSize.equalsIgnoreCase(String.valueOf(FONT_SIZE.xl))) {
                subviewTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, english ? mContext.getResources().getDimension(R.dimen.sp_17) : mContext.getResources().getDimension(R.dimen.sp_17_2));
            } else if (fontSize.equalsIgnoreCase(String.valueOf(FONT_SIZE.l))) {
                subviewTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, english ? mContext.getResources().getDimension(R.dimen.sp_15) : mContext.getResources().getDimension(R.dimen.sp_15_2));
            } else if (fontSize.equalsIgnoreCase(String.valueOf(FONT_SIZE.m))) {
                subviewTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, english ? mContext.getResources().getDimension(R.dimen.sp_13) : mContext.getResources().getDimension(R.dimen.sp_13_2));
            } else if (fontSize.equalsIgnoreCase(String.valueOf(FONT_SIZE.s))) {
                subviewTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, english ? mContext.getResources().getDimension(R.dimen.sp_10) : mContext.getResources().getDimension(R.dimen.sp_10_2));
            }
        }

        if (fontColor != null) {
            subviewTV.setTextColor(colorFromHexDecimal(fontColor));
        }

        if (fontStyle != null) {
            if (fontStyle.equalsIgnoreCase(String.valueOf(FONT_STYLE.bold))) {
                applyFont(subviewTV, FONTS.HelveticaNeueBold);
            } else if (fontStyle.equalsIgnoreCase(String.valueOf(FONT_STYLE.medium))) {
                applyFont(subviewTV, FONTS.HelveticaNeueMedium);
            } else if (fontStyle.equalsIgnoreCase(String.valueOf(FONT_STYLE.normal))) {
                applyFont(subviewTV, FONTS.HelveticaNeue);
            }
        }
    }

    public void startNewActivity(Class activityToStart, Bundle extras, boolean isFinish) {
        Intent intent = new Intent(mContext, activityToStart);
        if (extras != null) {
            intent.putExtras(extras);

            if (extras.containsKey(EXTRA_SINGLE_TOP)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            }
        }
        mContext.startActivity(intent);
        ((BaseActivity) mContext).overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);

        if (isFinish)
            ((Activity) mContext).finish();
    }

    public void startNewActivity1(Class activityToStart, boolean addPref ,  boolean isFinish) {
        Intent intent = new Intent(mContext, activityToStart);

        intent.putExtra("addPref" , addPref);

        mContext.startActivity(intent);
        ((BaseActivity) mContext).overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);

        if (isFinish)
            ((Activity) mContext).finish();
    }

    /**
     * startHomeActivity method removes all the activities from stack and only the called activity is comes in top
     *
     * @param activityToStart Class
     * @param bundle          Bundle
     */
    public void startHomeActivity(Class activityToStart, Bundle bundle) {
        Intent i = new Intent(mContext, activityToStart);

        if (bundle != null)
            i.putExtras(bundle);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(i);
        ((BaseActivity) mContext).overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
        ((Activity) mContext).finish();
    }

    public void startActivityForResult(Activity launchingActivity, Class activityToStart, Bundle bundle, int requestCode) {
        Intent intent = new Intent(mContext, activityToStart);
        intent.putExtras(bundle);
        launchingActivity.startActivityForResult(intent, requestCode);
        launchingActivity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
    }


    public boolean isInvalidEmail(String email) {
        Pattern EMAIL_PATTERN = Pattern.compile(
                "[a-zA-Z0-9+._%\\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"
        );
        return email == null || !EMAIL_PATTERN.matcher(email).matches();
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public int convertDpToPixel(float dp) {

        final float scale = mContext.getResources().getDisplayMetrics().density;
// Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }

    public int colorFromHexDecimal(String colorStr) {
        return Color.parseColor(colorStr);
    }

    public String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    public void clearAllNotifications(int NOTIFICATION_ID) {
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (NOTIFICATION_ID != -1) {
                notificationManager.cancel(NOTIFICATION_ID);
            } else {
                notificationManager.cancelAll();
            }
        }
    }

    public void doPhoneCall(final String phoneNum) {
        if (phoneNum == null || phoneNum.trim().isEmpty())
            return;
        showTwoBtnsMsgDialog("Are you sure you want to call <br/><b>" + phoneNum + "</b> <br/> number?", new SendMessageCallback() {
            @Override
            public void sendMsg(String message) {
                if (message.equalsIgnoreCase(CANCEL))
                    return;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNum));
                mContext.startActivity(intent);
            }
        });
    }

    public void sendEmail(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        mContext.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public boolean isPdfFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("pdf");
    }

    public void setVideoThumb(CustomNetworkImageView mediaGridNIV, String filePath) {

        Bitmap bmThumbnail;

//MICRO_KIND, size: 96 x 96 thumbnail
//        bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MICRO_KIND);
//        imageview_micro.setImageBitmap(bmThumbnail);

// MINI_KIND, size: 512 x 384 thumbnail
        bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, Thumbnails.MINI_KIND);
        mediaGridNIV.setLocalImageBitmap(bmThumbnail);

    }

    public void setVideoThumbFromUlr(final CustomNetworkImageView mediaGridNIV, final String videoPath) {

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            Bitmap bitmap = null;

            @Override
            protected Void doInBackground(Void... voids) {
                MediaMetadataRetriever mediaMetadataRetriever = null;
                try {
                    mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                    //   mediaMetadataRetriever.setDataSource(videoPath);
                    bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mediaMetadataRetriever != null) {
                        mediaMetadataRetriever.release();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (bitmap != null)
                    mediaGridNIV.setLocalImageBitmap(bitmap);
            }
        };
        asyncTask.execute();
    }

//    public String getCurrentTimeStamp() {
//        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
//    }

    public String getFutureExpiryTime() {
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 48); // adds one hour
        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault()).format(cal.getTime());
    }



    public boolean validateCNIC(PFAEditText cnicET, boolean showDailog) {
        String cnic = cnicET.getText().toString();
        cnic = cnic.replaceAll("-", "");
        if (cnic.isEmpty() || cnic.length() < 13) {
            if (showDailog) {
                showMsgDialog("Please enter valid CNIC number!", null);
            }
            return false;
        }
        return true;
    }

    public boolean validatePhoneNum(PFAEditText phoneNumET, boolean showDialog) {
        if (phoneNumET.getText().toString().isEmpty() || phoneNumET.getText().toString().length() < 11 || (!phoneNumET.getText().toString().startsWith("03"))) {
            if (showDialog)
                showMsgDialog("Please enter valid phone number!", null);
//            phoneNumET.setError("Invalid Phone number (11 digits starting with 03");
            return false;
        }
        return true;
    }


    public boolean isPackageExisted(String targetPackage){
        List<ApplicationInfo> packages;
        packages = mContext.getPackageManager().getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }





    public void shareOnWhatsApp(String shareHtmlStr) {

        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/html");

        String whatsAppPkg ="com.whatsapp";
        if(isPackageExisted("com.whatsapp"))
        {
            whatsAppPkg = "com.whatsapp";
        }
        else if(isPackageExisted("com.whatsapp.w4b"))
        {
            whatsAppPkg = "com.whatsapp.w4b";
        }

        whatsappIntent.setPackage(whatsAppPkg);
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareHtmlStr);
        try {
            mContext.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("Whatsapp have not been installed.");
        }
    }


    protected void replaceFragment(MenuMapFragment menuItemFragment) {
        final FragmentManager fragmentManager = ((BaseActivity) mContext).getSupportFragmentManager();
        try {
            fragmentManager.beginTransaction()
                    .replace(R.id.mapDetailLL, menuItemFragment)
                    .commit();

        } catch (Exception e) {
            fragmentManager.beginTransaction()
                    .replace(R.id.mapDetailLL, menuItemFragment)
                    .commitAllowingStateLoss();
        }

    }

//    public JSONObject getJSONFromAssetFile(String fileName) throws JSONException {
//
//        String tContents = null;
//
//        try {
//
//            InputStream is = mContext.getAssets().open(fileName);
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            tContents = new String(buffer, "UTF-8");
//
//        } catch (IOException e) {
//            printStackTrace(e);
//        }
//        return new JSONObject(tContents);
//
//    }


}