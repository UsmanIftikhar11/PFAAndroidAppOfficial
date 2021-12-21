package com.pfa.pfaapp;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.utils.AppConst;
import com.pfa.pfaapp.utils.DownloadFileManager;

import java.io.File;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_DELETE_IMAGE;

public class ImageGalleryActivity extends BaseActivity {

    CustomNetworkImageView galleryCNIV;
    ImageButton deleteImgBtn;
    View videoOverlay;
    VideoView myVideoView;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        url = bundle.getString(AppConst.EXTRA_DOWNLOAD_URL);
        galleryCNIV = findViewById(R.id.galleryCNIV);
        deleteImgBtn = findViewById(R.id.deleteImgBtn);
        videoOverlay = findViewById(R.id.videoOverlay);
        myVideoView = findViewById(R.id.videoView);

        sharedPrefUtils.printLog("Media Url==>", "" + url);

        if (url != null) {

            if (sharedPrefUtils.isVideoFile(url)) {
                MediaController mediacontroller = new MediaController(this);
                mediacontroller.setAnchorView(myVideoView);

                if (url.startsWith("http")) {
                    findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                    if (httpService.isNetworkDisconnected()) {
                        sharedPrefUtils.showMsgDialog(getString(R.string.no_internet_connection), null);
                        return;
                    }
                    sharedPrefUtils.showProgressDialog(true);
                    myVideoView.setVideoURI(Uri.parse(url));

                    myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            videoOverlay.setVisibility(View.GONE);
                            sharedPrefUtils.hideProgressDialog();
                            mp.start();
                        }
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
                    findViewById(R.id.downloadImgBtn).setVisibility(View.VISIBLE);
                    galleryCNIV.setImageUrl(url, AppController.getInstance().getImageLoader());
                } else {
                    galleryCNIV.setFileBitmap(url);
                }
            }
        }

        if (getIntent().getExtras().containsKey(EXTRA_DELETE_IMAGE)) {
            deleteImgBtn.setVisibility(View.VISIBLE);
            deleteImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
    }


    public void onClickBackImgBtn(View view) {
        onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    public void onClickDownloadBtn(View view) {
//         get the file name to be downloaded from url
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

}
