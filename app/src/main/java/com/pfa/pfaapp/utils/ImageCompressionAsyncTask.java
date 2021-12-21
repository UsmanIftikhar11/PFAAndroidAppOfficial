package com.pfa.pfaapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.iceteck.silicompressorr.SiliCompressor;
import com.pfa.pfaapp.interfaces.GetImgFileCallback;

import java.io.File;

public class ImageCompressionAsyncTask extends AsyncTask<Void, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private GetImgFileCallback getImgFileCallback;
    private File imageFile;
    ImageCompressionAsyncTask(File imageFile, Context context, GetImgFileCallback getImgFileCallback){
        this.mContext = context;
        this.getImgFileCallback=getImgFileCallback;
        this.imageFile=imageFile;

    }

    @Override
    protected String doInBackground(Void... params) {
        File localFile = new File(Environment.getExternalStorageDirectory()+"/DCIM");
        return SiliCompressor.with(mContext).compress(imageFile.getAbsolutePath(), localFile);
    }

    @Override
    protected void onPostExecute(String filePath) {

        if(filePath==null|| filePath.isEmpty())
            return;
        File imageFile = new File(filePath);

        getImgFileCallback.getImageFile(imageFile);



    }
}

