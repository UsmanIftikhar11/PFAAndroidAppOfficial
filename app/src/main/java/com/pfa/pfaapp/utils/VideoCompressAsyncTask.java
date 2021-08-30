package com.pfa.pfaapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.iceteck.silicompressorr.SiliCompressor;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.interfaces.GetImgFileCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;

import java.io.File;
import java.util.Locale;

public class VideoCompressAsyncTask extends AsyncTask<Void, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private GetImgFileCallback getImgFileCallback;
    private File videoFile;
    private File destDir;

    private CustomDialogs customDialogs;

    VideoCompressAsyncTask(File videoFile, File destDir, Context context, GetImgFileCallback getImgFileCallback) {
        this.mContext = context;
        this.getImgFileCallback = getImgFileCallback;
        this.videoFile = videoFile;
        this.destDir = destDir;
        customDialogs = new CustomDialogs(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        customDialogs.showProgressDialog(false);
        customDialogs.showToast(mContext.getString(R.string.video_compression_progress));
    }

    @Override
    protected String doInBackground(Void... paths) {

        return SiliCompressor.with(mContext).compressVideo(videoFile.getAbsolutePath(), destDir.getPath());
    }

    @Override
    protected void onPostExecute(String compressedFilePath) {
        super.onPostExecute(compressedFilePath);
        File imageFile = new File(compressedFilePath);
        float length = imageFile.length() / 1024f; // Size in KB
        String value;
        if (length >= 1024) {
            value = length / 1024f + " MB";

            if ((length / 1024f) > 3.0f) {
                customDialogs.showTwoBtnsMsgDialog("Max video size 3.0MB can be uploaded. Please compress the video with any of the Apps in Play store", new SendMessageCallback() {
                    @Override
                    public void sendMsg(String message) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=video%20compression&hl=en")));
                    }
                });
                return;
            }
        } else
            value = length + " KB";
        String text = String.format(Locale.US, "%s\nName: %s\nSize: %s", mContext.getString(R.string.video_compression_complete), imageFile.getName(), value);
//        Log.e("Video Size => ", " =" + text);
        customDialogs.hideProgressDialog();
        getImgFileCallback.getImageFile(imageFile);
    }
}
