package com.pfa.pfaapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.multidex.BuildConfig;

import android.util.Log;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.interfaces.GetImgFileCallback;
import com.pfa.pfaapp.interfaces.SendMessageCallback;
import com.pfa.pfaapp.interfaces.VideoFileCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.pfa.pfaapp.utils.AppConst.CAPTURE_PHOTO;
import static com.pfa.pfaapp.utils.AppConst.CHOOSE_FROM_GALLERY;
import static com.pfa.pfaapp.utils.AppConst.IMAGE_SELECTION_MAP;
import static com.pfa.pfaapp.utils.AppConst.MULTIPLE_IMAGES;
import static com.pfa.pfaapp.utils.AppConst.RECORD_VIDEO;
import static com.pfa.pfaapp.utils.AppConst.VIDEO_SELECTION_MAP;
import static com.pfa.pfaapp.utils.AppConst.mImageCaptureUri;

public class ImageSelectionUtils extends ScalingUtilities {
    private CustomNetworkImageView customNetworkImageView;
    private String imagePath;
    private Activity activity;
    private VideoFileCallback filePathCallback;
    private static String filePathOfCamera;

//    private static final int REQUEST_IMAGE_CAPTURE = 1000;


    public ImageSelectionUtils(Activity activity, CustomNetworkImageView customNetworkImageView) {
        super(activity);
        this.activity = activity;
        this.customNetworkImageView = customNetworkImageView;
        Log.d("imagePath" , "image selection utils");
    }


    public void showImagePickerDialog(VideoFileCallback filePathCallback, final boolean showVideoBtns, final boolean isMultiple) {
        this.filePathCallback = filePathCallback;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showSelectPictureDialog(new SendMessageCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void sendMsg(String message) {
                        filePathOfCamera = null;
                        int i = Integer.parseInt(message);
                        if (i == CAPTURE_PHOTO) {
                            Log.d("imagePath" , "image selection utils capture camera");
                            capturePicFromCamera();

                        } else if (i == CHOOSE_FROM_GALLERY) {
                            pickImageFromGallery(showVideoBtns);

                        } else if (i == MULTIPLE_IMAGES) {
                            pickMultipleImages();
                        } else if (i == RECORD_VIDEO) {
                            captureVideoFromCamera();
                        }
                    }
                }, showVideoBtns, isMultiple);
            }
        }, 100);
    }

    private void capturePicFromCamera() {

        Log.d("imagePath" , "capturePicFromCamera ()= " + BuildConfig.APPLICATION_ID);
        File fPath = Environment.getExternalStorageDirectory();
        final File f = new File(fPath, "" + (System.currentTimeMillis()) + ".png");

        filePathOfCamera = f.getAbsolutePath();

//        Uri mImageCaptureUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", f);
        Uri mImageCaptureUri = FileProvider.getUriForFile(activity, "com.pfaofficial.provider", f);

        Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoCaptureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        photoCaptureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        photoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        activity.startActivityForResult(photoCaptureIntent, CAPTURE_PHOTO);

        Log.d("imagePath" , "capturePicFromCamera ()= " + mImageCaptureUri.getPath());

//        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (pictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
//           activity.startActivityForResult(pictureIntent, CAPTURE_PHOTO);
//
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void pickImageFromGallery(boolean showVideoBtns) {
        Log.d("imagePath" , "pickImageFromGallery ()= " + BuildConfig.APPLICATION_ID);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (showVideoBtns) {
            intent.setType("*/*");
            String[] mimetypes = {"image/*", "video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        } else {
            intent.setType("image/*");
        }
        Log.d("imagePath" , "capturePicFromCamera ()= completed" );
        activity.startActivityForResult(intent, CHOOSE_FROM_GALLERY);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void pickMultipleImages() {
        Intent intent = new Intent(activity, AlbumSelectActivity.class);
        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 100);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE);

    }

    private void captureVideoFromCamera() {

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        if (takeVideoIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takeVideoIntent, RECORD_VIDEO);
        }
    }

    public void chooseFromCameraImgPath(Intent data, SendMessageCallback callback) {
        try {
            Log.d("imagePath" , "chooseFromCameraImgPath = " + imagePath);

            if (filePathOfCamera != null) {
                imagePath = filePathOfCamera;

            } else if (data != null && data.getExtras() != null && data.getExtras().containsKey("data")) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                File fPath = Environment.getExternalStorageDirectory();
                final File f = new File(fPath, "" + (System.currentTimeMillis()) + ".png");

                FileOutputStream stream = new FileOutputStream(f);
                assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                bitmap.recycle();
                stream.close();

                imagePath = f.getAbsolutePath();
                Log.d("imagePath" , "path = " + imagePath);
            }

            setImageInNIV(callback);


        } catch (IOException e) {
            printStackTrace(e);
            Log.d("imagePath" , "chooseFromCameraImgPath error = " + e.toString());
        }

    }

    public void chooseFromGalleryImgPath(Intent data, SendMessageCallback callback) {

        if (filePathOfCamera != null) {
            imagePath = filePathOfCamera;
        } else {
            if (data != null && data.getData() != null) {
                mImageCaptureUri = data.getData();
                imagePath = FilePathUtils.getPath(activity, mImageCaptureUri);
            }
        }

        Log.d("imagePath" , " galleryPath = " + imagePath);
        setImageInNIV(callback);

    }

    public void chooseMultipleImages(String filePath, VideoFileCallback filePathCallback) {
        this.filePathCallback = filePathCallback;

        imagePath =filePath;
        setImageInNIV(null);

    }

    private void setVideoThumb() {
        VIDEO_SELECTION_MAP.put(customNetworkImageView.getTag().toString(), imagePath);
        if (filePathCallback != null) {
            filePathCallback.onFileSelected(imagePath);
        }

    }

    private void setImageInNIV(final SendMessageCallback callback) {

        if (imagePath != null) {
            final File[] imageFile = {new File(imagePath)};
            if (imageFile[0].exists()) {

                if (isVideoFile(imagePath)) {
                    final float reqVidSizeInMbs =3.0f * 1024 * 1024;// 3.0MB to byes //1.5f * 1024 * 1024;  // 1.5MB to bytes
                    final float maxVidSizeToCompress = 64.0f * 1024 * 1024;  // 60MB to bytes

                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/pfaTemp/videos");
                    if (f.mkdirs() || f.isDirectory()) {

                        float vidBeforeCompression = imageFile[0].length() / (1024f * 1024f);
                        Log.e("Video Size==>", "Video Before Compression==> " + vidBeforeCompression + " MB");

                        if (imageFile[0].length() > maxVidSizeToCompress) {
                            showMsgDialog("Video Size is more than (60MB). Please compress the video first and try again!", null);
                            return;
                        }

                        if (filePathCallback != null)
                            filePathCallback.videoSelected();

                        new VideoCompressAsyncTask(imageFile[0], f, mContext, new GetImgFileCallback() {
                            @Override
                            public void getImageFile(File videoFile) {

                                if (videoFile.length() <= reqVidSizeInMbs) {
                                    imagePath = videoFile.getAbsolutePath();
                                    setVideoThumb();

                                } else {
                                    showMsgDialog("Select video size is more than (3.0MB). Please compress the video first and try again!", null);

                                }
                            }
                        }).execute();
                    } else {
                        setVideoThumb();
                    }

                } else {

                    new ImageCompressionAsyncTask(imageFile[0], mContext, new GetImgFileCallback() {
                        @Override
                        public void getImageFile(File scaledFile) {
                            imageFile[0] = scaledFile;
                            imagePath = imageFile[0].getAbsolutePath();

                            Bitmap bitmap = getBitmap(imagePath);
                            customNetworkImageView.setLocalImageBitmap(bitmap);

                            IMAGE_SELECTION_MAP.put(customNetworkImageView.getTag().toString(), imagePath);
                            customNetworkImageView.setImageFile(imageFile[0]);

                            if (filePathCallback != null) {
                                filePathCallback.onFileSelected(imagePath);
                            }

                            if (callback != null)
                                callback.sendMsg("Choose Image");

                        }
                    }).execute();
                }
            }
        }
    }

        private Bitmap getBitmap(String imagePath) {
            Bitmap bitmap=null;
            try {
                File f= new File(imagePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap ;
        }

}
