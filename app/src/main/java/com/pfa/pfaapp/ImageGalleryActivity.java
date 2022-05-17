package com.pfa.pfaapp;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_DELETE_IMAGE;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.viewpager2.widget.ViewPager2;

import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.DownloadFileManager;
import com.pfa.pfaapp.utils.OnSwipeTouchListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageGalleryActivity extends BaseActivity {

    CustomNetworkImageView galleryCNIV;
    ImageButton deleteImgBtn;
    View videoOverlay;
    VideoView myVideoView;
    String url ;
    int position;
    List<String> imagesList = new ArrayList<>();
    private RelativeLayout viewPagerGallery;
    boolean firtImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        url = bundle.getString(AppConst.EXTRA_DOWNLOAD_URL);
        imagesList = new ArrayList<>();
        if (bundle.containsKey(AppConst.EXTRA_IMAGE_POSITION) && bundle.containsKey(AppConst.EXTRA_IMAGES_LIST)) {
            position = Integer.parseInt(bundle.getString(AppConst.EXTRA_IMAGE_POSITION));
            imagesList = bundle.getStringArrayList(AppConst.EXTRA_IMAGES_LIST);
        }
        galleryCNIV = findViewById(R.id.galleryCNIV);
        deleteImgBtn = findViewById(R.id.deleteImgBtn);
        viewPagerGallery = findViewById(R.id.viewPagerGallery);
        videoOverlay = findViewById(R.id.videoOverlay);
        myVideoView = findViewById(R.id.videoView);

        sharedPrefUtils.printLog("Media Url==>", "" + url);
        Log.d("ImageListSize" , "size = " + imagesList.size());

        if (!firtImage){
            if (url != null) {

                Log.d("ImageListUrl" , "onSwipeRight url1 = " + url);
                Log.d("CheckVideoUrl" , "onSwipeLeft position = " + 13);
                if (sharedPrefUtils.isVideoFile(url)) {
                    Log.d("CheckVideoUrl" , "onSwipeLeft position = " + 11);
                    galleryCNIV.setVisibility(View.GONE);
                    myVideoView.setVisibility(View.VISIBLE);
                    videoOverlay.setVisibility(View.VISIBLE);
                    MediaController mediacontroller = new MediaController(ImageGalleryActivity.this);
                    mediacontroller.setAnchorView(myVideoView);

                    if (url.startsWith("http")) {
                        Log.d("CheckVideoUrl" , "onSwipeLeft position = " + 12);
                        findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                        if (httpService.isNetworkDisconnected()) {
                            sharedPrefUtils.showMsgDialog(getString(R.string.no_internet_connection), null);
                            return;
                        }
                        sharedPrefUtils.showProgressDialog(true);
                        myVideoView.setVideoURI(Uri.parse(url));

                        Log.d("CheckVideoUrl" , "onSwipeLeft position = " + 14);
                        myVideoView.setOnPreparedListener(mp -> {
                            Log.d("CheckVideoUrl" , "onSwipeLeft position = " + 15);
                            videoOverlay.setVisibility(View.GONE);
                            sharedPrefUtils.hideProgressDialog();
                            mp.start();
                        });
                    } else {
                        videoOverlay.setVisibility(View.GONE);
                        myVideoView.setVideoPath(url);
                    }
                    myVideoView.setMediaController(mediacontroller);
                    myVideoView.requestFocus();
                    myVideoView.start();

                } else {
                    myVideoView.setVisibility(View.GONE);
                    videoOverlay.setVisibility(View.GONE);
                    galleryCNIV.setVisibility(View.VISIBLE);
                    if (url.contains("http")) {
                        Log.d("imageCheck" , "ImageGalleryActivity online image = ");
                        findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                        if (url.endsWith("pdf")) {
                            Log.d("imageCheck" , "ImageGalleryActivity online image = pdf");
                            galleryCNIV.setDrawable(R.drawable.pdf_large);
                        }
                        else if (url.endsWith("docx"))
                            galleryCNIV.setDrawable(R.drawable.doc_large);
                        else {
                            Log.d("imageCheck" , "ImageGalleryActivity online image = others");
                            galleryCNIV.setImageUrl(url, AppController.getInstance().getImageLoader());
                        }
                    } else {
                        Log.d("imageCheck" , "ImageGalleryActivity offline image = ");
                        galleryCNIV.setFileBitmap(url);
                    }
                }
            }
            firtImage = true;
        }

        if (position>=0 && imagesList != null && !imagesList.isEmpty()) {
            viewPagerGallery.setOnTouchListener(new OnSwipeTouchListener(ImageGalleryActivity.this) {
                public void onSwipeTop() {
//                    Toast.makeText(ImageGalleryActivity.this, "top", Toast.LENGTH_SHORT).show();
                }

                public void onSwipeRight() {
                    if (position > 0) {
                        position = position - 1;
                        url = imagesList.get(position);
                        Log.d("ImageListUrl", "onSwipeRight url = " + url);
                        Log.d("ImageListUrl", "onSwipeLeft position = " + position);
                        if (url != null) {

                            if (sharedPrefUtils.isVideoFile(url)) {
                                galleryCNIV.setVisibility(View.GONE);
                                myVideoView.setVisibility(View.VISIBLE);
                                videoOverlay.setVisibility(View.VISIBLE);
                                MediaController mediacontroller = new MediaController(ImageGalleryActivity.this);
                                mediacontroller.setAnchorView(myVideoView);

                                if (url.startsWith("http")) {
                                    findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                                    if (httpService.isNetworkDisconnected()) {
                                        sharedPrefUtils.showMsgDialog(getString(R.string.no_internet_connection), null);
                                        return;
                                    }
                                    sharedPrefUtils.showProgressDialog(true);
                                    myVideoView.setVideoURI(Uri.parse(url));

                                    myVideoView.setOnPreparedListener(mp -> {
                                        videoOverlay.setVisibility(View.GONE);
                                        sharedPrefUtils.hideProgressDialog();
                                        mp.start();
                                    });
                                } else {
                                    videoOverlay.setVisibility(View.GONE);
                                    myVideoView.setVideoPath(url);
                                }
                                myVideoView.setMediaController(mediacontroller);
                                myVideoView.requestFocus();
                                myVideoView.start();

                            } else {
                                myVideoView.setVisibility(View.GONE);
                                videoOverlay.setVisibility(View.GONE);
                                galleryCNIV.setVisibility(View.VISIBLE);
                                if (url.contains("http")) {
                                    Log.d("imageCheck", "ImageGalleryActivity online image = ");
                                    findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                                    if (url.endsWith("pdf")) {
                                        Log.d("imageCheck", "ImageGalleryActivity online image = pdf");
                                        galleryCNIV.setDrawable(R.drawable.pdf_large);
                                    } else if (url.endsWith("docx"))
                                        galleryCNIV.setDrawable(R.drawable.doc_large);
                                    else {
                                        Log.d("imageCheck", "ImageGalleryActivity online image = others");
                                        galleryCNIV.setImageUrl(url, AppController.getInstance().getImageLoader());
                                    }
                                } else {
                                    Log.d("imageCheck", "ImageGalleryActivity offline image = ");
                                    galleryCNIV.setFileBitmap(url);
                                }
                            }
                        }
                    }
//                    Toast.makeText(ImageGalleryActivity.this, "right", Toast.LENGTH_SHORT).show();
                }

                public void onSwipeLeft() {
                    if (position < imagesList.size() - 1) {
                        position = position + 1;
                        url = imagesList.get(position);
                        Log.d("ImageListUrl", "onSwipeLeft url = " + url);
                        Log.d("ImageListUrl", "onSwipeLeft position = " + position);
                        Log.d("ImageListUrl", "onSwipeLeft position = " + imagesList.size());
                        if (url != null) {

                            Log.d("CheckVideoUrl", "onSwipeLeft position = " + 1);
                            if (sharedPrefUtils.isVideoFile(url)) {
                                Log.d("CheckVideoUrl", "onSwipeLeft position = " + 2);
                                galleryCNIV.setVisibility(View.GONE);
                                myVideoView.setVisibility(View.VISIBLE);
                                videoOverlay.setVisibility(View.VISIBLE);
                                MediaController mediacontroller = new MediaController(ImageGalleryActivity.this);
                                mediacontroller.setAnchorView(myVideoView);

                                if (url.startsWith("http")) {
                                    Log.d("CheckVideoUrl", "onSwipeLeft position = " + 3);
                                    findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                                    if (httpService.isNetworkDisconnected()) {
                                        sharedPrefUtils.showMsgDialog(getString(R.string.no_internet_connection), null);
                                        return;
                                    }
                                    sharedPrefUtils.showProgressDialog(true);
                                    myVideoView.setVideoURI(Uri.parse(url));

                                    Log.d("CheckVideoUrl", "onSwipeLeft position = " + 4);
                                    myVideoView.setOnPreparedListener(mp -> {
                                        Log.d("CheckVideoUrl", "onSwipeLeft position = " + 5);
                                        videoOverlay.setVisibility(View.GONE);
                                        sharedPrefUtils.hideProgressDialog();
                                        mp.start();
                                    });
                                } else {
                                    videoOverlay.setVisibility(View.GONE);
                                    myVideoView.setVideoPath(url);
                                }
                                myVideoView.setMediaController(mediacontroller);
                                myVideoView.requestFocus();
                                myVideoView.start();

                            } else {
                                myVideoView.setVisibility(View.GONE);
                                videoOverlay.setVisibility(View.GONE);
                                galleryCNIV.setVisibility(View.VISIBLE);
                                if (url.contains("http")) {
                                    Log.d("imageCheck", "ImageGalleryActivity online image = ");
                                    findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                                    if (url.endsWith("pdf")) {
                                        Log.d("imageCheck", "ImageGalleryActivity online image = pdf");
                                        galleryCNIV.setDrawable(R.drawable.pdf_large);
                                    } else if (url.endsWith("docx"))
                                        galleryCNIV.setDrawable(R.drawable.doc_large);
                                    else {
                                        Log.d("imageCheck", "ImageGalleryActivity online image = others");
                                        galleryCNIV.setImageUrl(url, AppController.getInstance().getImageLoader());
                                    }
                                } else {
                                    Log.d("imageCheck", "ImageGalleryActivity offline image = ");
                                    galleryCNIV.setFileBitmap(url);
                                }
                            }
                        }
                    }
//                    Toast.makeText(ImageGalleryActivity.this, "left", Toast.LENGTH_SHORT).show();
                }

                public void onSwipeBottom() {
//                    Toast.makeText(ImageGalleryActivity.this, "bottom", Toast.LENGTH_SHORT).show();
                }

            });
        }
        /*if (url != null) {

            if (sharedPrefUtils.isVideoFile(url)) {
                MediaController mediacontroller = new MediaController(ImageGalleryActivity.this);
                mediacontroller.setAnchorView(myVideoView);

                if (url.startsWith("http")) {
                    findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                    if (httpService.isNetworkDisconnected()) {
                        sharedPrefUtils.showMsgDialog(getString(R.string.no_internet_connection), null);
                        return;
                    }
                    sharedPrefUtils.showProgressDialog(true);
                    myVideoView.setVideoURI(Uri.parse(url));

                    myVideoView.setOnPreparedListener(mp -> {
                        videoOverlay.setVisibility(View.GONE);
                        sharedPrefUtils.hideProgressDialog();
                        mp.start();
                    });
                } else {
                    videoOverlay.setVisibility(View.GONE);
                    myVideoView.setVideoPath(url);
                }
                myVideoView.setMediaController(mediacontroller);
                myVideoView.requestFocus();
                myVideoView.start();

            } else {
                myVideoView.setVisibility(View.GONE);
                videoOverlay.setVisibility(View.GONE);
                galleryCNIV.setVisibility(View.VISIBLE);
                if (url.contains("http")) {
                    Log.d("imageCheck" , "ImageGalleryActivity online image = ");
                    findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                    if (url.endsWith("pdf")) {
                        Log.d("imageCheck" , "ImageGalleryActivity online image = pdf");
                        galleryCNIV.setDrawable(R.drawable.pdf_large);
                    }
                    else if (url.endsWith("docx"))
                        galleryCNIV.setDrawable(R.drawable.doc_large);
                    else {
                        Log.d("imageCheck" , "ImageGalleryActivity online image = others");
                        galleryCNIV.setImageUrl(url, AppController.getInstance().getImageLoader());
                    }
                } else {
                    Log.d("imageCheck" , "ImageGalleryActivity offline image = ");
                    galleryCNIV.setFileBitmap(url);
                }
            }
        }*/

//        if (imagesList.size()>0){
//
//        }
        if (getIntent().getExtras().containsKey(EXTRA_DELETE_IMAGE)) {
            deleteImgBtn.setVisibility(View.VISIBLE);
            deleteImgBtn.setOnClickListener(v -> {
                setResult(RESULT_OK);
                finish();
            });
        }
    }


    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    public void onClickDownloadBtn(View view) {
//         get the file name to be downloaded from url
        Log.d("onClickDownloadBtn", " url click= " + url);
        final String outputFile = URLUtil.guessFileName(url, null, null);

//        Create the folder for downloads
        String rootDir = Environment.getExternalStorageDirectory() + File.separator + "Download";
//                + File.separator + "PFA_Mobile_Downloads";
        File rootFile = new File(rootDir);

        if (!rootFile.exists())
            rootFile.mkdir();

//        End create folder

        if (sharedPrefUtils.isVideoFile(url)) {
            DownloadFileManager.downloadVideo(url, new File(rootFile, outputFile), pbListener, this);
        } else {
            DownloadFileManager.downloadImage(url, new File(rootFile, outputFile), pbListener, this);
        }

    }

    DownloadFileManager.OnDownloadListener pbListener = new DownloadFileManager.OnDownloadListener() {
        @Override
        public void onStart() {

            sharedPrefUtils.showProgressDialog(true);
        }

        @Override
        public void onSetMax(int max) {

        }

        @Override
        public void onProgress(int current) {

        }

        @Override
        public void onFinishDownload() {
            sharedPrefUtils.hideProgressDialog();
        }

        @Override
        public void onResponse(boolean isSuccess, String path) {
            sharedPrefUtils.hideProgressDialog();
            if (isSuccess) {
                sharedPrefUtils.showMsgDialog("File downloaded Successfully" + "\n" + path, null);
//                Toast.makeText(ImageGalleryActivity.this, ""+path, Toast.LENGTH_LONG).show();
            } else {
                sharedPrefUtils.showMsgDialog("File downloading Failed!!", null);
            }
        }
    };

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("User-agent","Mozilla/4.0");
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
    }



}
