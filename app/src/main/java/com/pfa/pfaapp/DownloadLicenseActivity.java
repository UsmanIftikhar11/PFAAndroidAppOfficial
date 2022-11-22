package com.pfa.pfaapp;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_DETAIL_MENU;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_JSON_STR_RESPONSE;
import static com.pfa.pfaapp.utils.AppConst.EXTRA_URL_TO_CALL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.RotateDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;
import com.pfa.pfaapp.interfaces.HttpResponseCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.rey.material.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DownloadLicenseActivity extends BaseActivity implements HttpResponseCallback {

    private TextView txtDateTime , txtAppUrl , txtSignature , txtWarning , txtVerifyQrCode;
    private TextView txtBusinessNameEng , txtLicenseDurationVal1 , txtLicenseDurationVal2 , txtLicenseNumberVal , txtOwnerNameVal ,
            txtBusinessAddressVal , txtCNICVal , txtLicenseCategoryVal;
    private TextView txtLicenseDuration , txtLicenseNumber , txtOwnerName , txtBusinessAddress , txtCNIC , txtLicenseCategory;
    private TextView txtBusinessNameUrdu;
    private TextView txtLicenseDurationVal1Urdu;
    private TextView txtLicenseDurationVal2Urdu;
    private TextView txtLicenseNumberValUrdu;
    private TextView txtOwnerNameValUrdu;
    private TextView txtBusinessAddressValUrdu;
    private TextView txtCNICValUrdu;
    private TextView txtLicenseCategoryValUrdu;
    private TextView txtLicenseDurationUrdu , txtLicenseNumberUrdu , txtOwnerNameUrdu , txtBusinessAddressUrdu , txtCNICUrdu , txtLicenseCategoryUrdu;
    private ImageView imgQrCode , imgSignature;
    private String api_url;
    private ConstraintLayout clMain;
    private String currentDate , currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_license);

        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());

        sharedPrefUtils.showProgressDialog(false);

        initViews();
        getIntentData();

        txtDateTime.setText(currentDate + ", " + currentTime);
    }

    private void initViews() {
        txtDateTime = findViewById(R.id.txtDateTime);
        txtAppUrl = findViewById(R.id.txtAppUrl);
        txtBusinessNameEng = findViewById(R.id.txtBusinessNameEng);
        txtLicenseDurationVal1 = findViewById(R.id.txtLicenseDurationVal1);
        txtLicenseDurationVal2 = findViewById(R.id.txtLicenseDurationVal2);
        txtLicenseNumberVal = findViewById(R.id.txtLicenseNumberVal);
        txtOwnerNameVal = findViewById(R.id.txtOwnerNameVal);
        txtBusinessAddressVal = findViewById(R.id.txtBusinessAddressVal);
        txtCNICVal = findViewById(R.id.txtCNICVal);
        txtLicenseCategoryVal = findViewById(R.id.txtLicenseCategoryVal);
        txtBusinessNameUrdu = findViewById(R.id.txtBusinessNameUrdu);
        txtLicenseDurationVal1Urdu = findViewById(R.id.txtLicenseDurationVal1Urdu);
        txtLicenseDurationVal2Urdu = findViewById(R.id.txtLicenseDurationVal2Urdu);
        txtLicenseNumberValUrdu = findViewById(R.id.txtLicenseNumberValUrdu);
        txtOwnerNameValUrdu = findViewById(R.id.txtOwnerNameValUrdu);
        txtBusinessAddressValUrdu = findViewById(R.id.txtBusinessAddressValUrdu);
        txtCNICValUrdu = findViewById(R.id.txtCNICValUrdu);
        txtLicenseCategoryValUrdu = findViewById(R.id.txtLicenseCategoryValUrdu);
        imgQrCode = findViewById(R.id.imgQrCode);
        imgSignature = findViewById(R.id.imgSignature);
        clMain = findViewById(R.id.clMain);

        txtLicenseDuration = findViewById(R.id.txtLicenseDuration);
        txtLicenseNumber = findViewById(R.id.txtLicenseNumber);
        txtOwnerName = findViewById(R.id.txtOwnerName);
        txtBusinessAddress = findViewById(R.id.txtBusinessAddress);
        txtCNIC = findViewById(R.id.txtCNIC);
        txtLicenseCategory = findViewById(R.id.txtLicenseCategory);
        txtLicenseDurationUrdu = findViewById(R.id.txtLicenseDurationUrdu);
        txtLicenseNumberUrdu = findViewById(R.id.txtLicenseNumberUrdu);
        txtOwnerNameUrdu = findViewById(R.id.txtOwnerNameUrdu);
        txtBusinessAddressUrdu = findViewById(R.id.txtBusinessAddressUrdu);
        txtCNICUrdu = findViewById(R.id.txtCNICUrdu);
        txtLicenseCategoryUrdu = findViewById(R.id.txtLicenseCategoryUrdu);

        txtSignature = findViewById(R.id.txtSignature);
        txtVerifyQrCode = findViewById(R.id.txtVerifyQrCode);
        txtWarning = findViewById(R.id.txtWarning);

        Typeface typefaceEng = Typeface.createFromAsset(getAssets(), "fonts/FallingSky.otf");
        Typeface typefaceEngTitle = Typeface.createFromAsset(getAssets(), "fonts/FallingSky.otf");
        Typeface typefaceUrdu = Typeface.createFromAsset(getAssets(), "fonts/NotoNastaliqUrdu-Regular.ttf");
        Typeface typefaceUrduTitle = Typeface.createFromAsset(getAssets(), "fonts/NotoNastaliqUrdu-Bold.ttf");
        txtDateTime.setTypeface(typefaceEng);
        txtAppUrl.setTypeface(typefaceEng);
        txtBusinessNameEng.setTypeface(typefaceEngTitle);
        txtLicenseDurationVal1.setTypeface(typefaceEng);
        txtLicenseDurationVal2.setTypeface(typefaceEng);
        txtLicenseNumberVal.setTypeface(typefaceEng);
        txtOwnerNameVal.setTypeface(typefaceEng);
        txtBusinessAddressVal.setTypeface(typefaceEng);
        txtCNICVal.setTypeface(typefaceEng);
        txtLicenseCategoryVal.setTypeface(typefaceEng);
        txtBusinessNameUrdu.setTypeface(typefaceEngTitle);
        txtLicenseDurationVal1Urdu.setTypeface(typefaceEngTitle);
        txtLicenseDurationVal2Urdu.setTypeface(typefaceEngTitle);
        txtLicenseNumberValUrdu.setTypeface(typefaceEngTitle);
//        txtOwnerNameValUrdu.setTypeface(typefaceEngTitle);
//        txtBusinessAddressValUrdu.setTypeface(typefaceEngTitle);
        txtCNICValUrdu.setTypeface(typefaceEngTitle);
//        txtLicenseCategoryValUrdu.setTypeface(typefaceEngTitle);

        txtLicenseDuration.setTypeface(typefaceEngTitle);
        txtLicenseNumber.setTypeface(typefaceEngTitle);
        txtOwnerName.setTypeface(typefaceEngTitle);
        txtBusinessAddress.setTypeface(typefaceEngTitle);
        txtCNIC.setTypeface(typefaceEngTitle);
        txtLicenseCategory.setTypeface(typefaceEngTitle);
//        txtLicenseDurationUrdu.setTypeface(typefaceEngTitle);
//        txtLicenseNumberUrdu.setTypeface(typefaceEngTitle);
//        txtOwnerNameUrdu.setTypeface(typefaceEngTitle);
//        txtBusinessAddressUrdu.setTypeface(typefaceEngTitle);
//        txtCNICUrdu.setTypeface(typefaceEngTitle);
//        txtLicenseCategoryUrdu.setTypeface(typefaceEngTitle);
//
//        txtSignature.setTypeface(typefaceEngTitle);
        txtVerifyQrCode.setTypeface(typefaceEngTitle);
        txtWarning.setTypeface(typefaceUrduTitle);

    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(EXTRA_URL_TO_CALL)) {
            api_url = getIntent().getStringExtra(EXTRA_URL_TO_CALL);
            api_url = api_url.substring(38 , 44);
            txtAppUrl.setText("https://cell.pfa.gop.pk/license/download/"+api_url);
        }
        if (bundle != null && bundle.containsKey(EXTRA_JSON_STR_RESPONSE)) {
            if (getIntent().hasExtra(EXTRA_JSON_STR_RESPONSE)) {
                String responseStr = getIntent().getStringExtra(EXTRA_JSON_STR_RESPONSE);
                try {
                    JSONObject responseJSONObject = new JSONObject(responseStr);
                    onCompleteHttpResponse(responseJSONObject, null);
                    Log.d("BusinessDetailsMenu", "PFADetailActivity = ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onCompleteHttpResponse(JSONObject response, String requestUrl) {
        if (response != null && response.optBoolean("status")) {
            if (response.has("data")) {
                Log.d("enfrocementData" , "dada = " + response);
                try {
                    JSONObject jsonObject = response.getJSONObject("data");
                    populateData(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void populateData(JSONObject jsonObject) {
        try {
            txtBusinessNameEng.setText(jsonObject.getString("business_english"));
            txtLicenseDurationVal1.setText(jsonObject.getString("issue"));
            txtLicenseDurationVal2.setText(jsonObject.getString("expiry"));
            txtLicenseNumberVal.setText(jsonObject.getString("license_number"));
            txtOwnerNameVal.setText(jsonObject.getString("owner_english"));
            txtBusinessAddressVal.setText(jsonObject.getString("address_english"));
            txtCNICVal.setText(jsonObject.getString("cnic_number"));
            txtLicenseCategoryVal.setText(jsonObject.getString("item_title"));
            txtBusinessNameUrdu.setText(jsonObject.getString("business_urdu"));
            txtLicenseDurationVal1Urdu.setText(jsonObject.getString("issue"));
            txtLicenseDurationVal2Urdu.setText(jsonObject.getString("expiry"));
            txtLicenseNumberValUrdu.setText(jsonObject.getString("license_number"));
            txtOwnerNameValUrdu.setText(jsonObject.getString("owner_urdu"));
            txtBusinessAddressValUrdu.setText(jsonObject.getString("address_urdu"));
            txtCNICValUrdu.setText(jsonObject.getString("cnic_number"));
            txtLicenseCategoryValUrdu.setText(jsonObject.getString("item_urdu_title"));
            Glide.with(this).load(jsonObject.getString("qrcode")).into(imgQrCode);
            Glide.with(this).load(jsonObject.getString("dg_signature")).into(imgSignature);

            if (jsonObject.getString("business_english").length()>24){
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) txtBusinessNameUrdu.getLayoutParams();
                layoutParams.endToEnd = R.id.clCertificate;
                layoutParams.startToStart = R.id.clCertificate;
                layoutParams.topMargin = (int) getResources().getDimension(R.dimen._77sdp);
                layoutParams.rightMargin = 0;
                txtBusinessNameUrdu.setLayoutParams(layoutParams);

                ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) txtBusinessNameEng.getLayoutParams();
                layoutParams1.endToEnd = R.id.clCertificate;
                layoutParams1.startToStart = R.id.clCertificate;
                layoutParams1.topToBottom = R.id.clCertificate;
                layoutParams1.topMargin = (int) getResources().getDimension(R.dimen._90sdp);
                layoutParams1.leftMargin = 0;
                txtBusinessNameEng.setLayoutParams(layoutParams1);
                txtWarning.setTranslationY(-20);
            } else {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) txtBusinessNameUrdu.getLayoutParams();
                layoutParams.endToEnd = R.id.clCertificate;
                layoutParams.topToTop = R.id.clCertificate;
//                layoutParams.startToStart = R.id.clCertificate;
                layoutParams.topMargin = (int) getResources().getDimension(R.dimen._85sdp);
                layoutParams.rightMargin = (int) getResources().getDimension(R.dimen._35sdp);
                txtBusinessNameUrdu.setLayoutParams(layoutParams);

                ConstraintLayout.LayoutParams layoutParams1 = (ConstraintLayout.LayoutParams) txtBusinessNameEng.getLayoutParams();
//                layoutParams1.endToEnd = R.id.clCertificate;
                layoutParams1.startToStart = R.id.clCertificate;
                layoutParams1.topToTop = R.id.clCertificate;
                layoutParams1.topMargin = (int) getResources().getDimension(R.dimen._85sdp);
                layoutParams1.leftMargin = (int) getResources().getDimension(R.dimen._45sdp);
                txtBusinessNameEng.setLayoutParams(layoutParams1);
                txtWarning.setTranslationY(0);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutToImage();
                }
            } , 1500);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    String dirpath;
    java.io.File photo;
    public void layoutToImage(/*View view*/) {
        // get view group using reference
//        relativeLayout = (ConstraintLayout) view.findViewById(R.id.print);
        // convert view group to bitmap
        clMain.setDrawingCacheEnabled(true);
        clMain.buildDrawingCache();
        Bitmap bm = clMain.getDrawingCache();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bytes);
//        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
//        File imagesFolder = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOWNLOADS)));
//
//        photo = new File(imagesFolder+ "/layout.jpg");

        photo = new java.io.File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/Filename.jpg");
        try {
//            if (!photo.exists())
                photo.createNewFile();
            FileOutputStream fo = new FileOutputStream(photo);
            fo.write(bytes.toByteArray());
            imageToPDF();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void imageToPDF() throws FileNotFoundException {
        try {
            Document document = new Document();
            //            document.setPageSize(PageSize.A4.rotate());
//            document.newPage();

            dirpath = android.os.Environment.getExternalStorageDirectory().toString();
            currentDate = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
            currentTime = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
            String pdfName = "/FoodLicense"+currentDate+currentTime+".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + pdfName)); //  Change pdf's name.

            document.open();
            Image img = Image.getInstance(String.valueOf(photo));
            float scaler = ((document.getPageSize().getHeight() - document.topMargin()
                    - document.bottomMargin() - 0) / img.getWidth()) * 100;

            img.scalePercent(scaler);
            img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
            img.setRotationDegrees(90);
            document.add(img);
            document.close();
//            Toast.makeText(this, "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
            sharedPrefUtils.hideProgressDialog();
            sharedPrefUtils.showMsgDialog("Food Business License Saved Successfully! \n" +
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + pdfName, new SendMessageCallback() {
                @Override
                public void sendMsg(String message) {
                    File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            , pdfName.substring(1));
                    Uri uri = Uri.fromFile(outputFile);
                    Uri fileURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", outputFile);


                    Intent share = new Intent();
//                    share.setAction(Intent.ACTION_VIEW);
                    share.setAction(Intent.ACTION_SEND);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_STREAM, fileURI);
//                    share.setPackage("com.whatsapp");
                    startActivity(share);

                    /*Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setDataAndType(fileURI, "application/pdf");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, fileURI);
//                    sendIntent.putExtra(Intent.EXTRA_STREA, uri);

                    Intent openIntent = new Intent(Intent.ACTION_VIEW);
                    openIntent.setDataAndType(fileURI, "application/pdf");
                    openIntent.putExtra(Intent.EXTRA_STREAM, fileURI);
                    openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                    Intent chooserIntent = Intent.createChooser(sendIntent,"Share Via");

                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, openIntent);

                    startActivity(chooserIntent);*/

                }
            });
        } catch (Exception e) {
            sharedPrefUtils.hideProgressDialog();
        }
    }
}