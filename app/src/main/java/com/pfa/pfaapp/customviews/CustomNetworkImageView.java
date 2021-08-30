package com.pfa.pfaapp.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.models.FormFieldInfo;

import java.io.File;

/**
 * Custom NetworkImageView (volley library): By default it only accepts the url to download and set the image but its modified here to accept:
 * bitmap, drawable, File and  url to download remote image as well
 */
public class CustomNetworkImageView extends NetworkImageView {

    private Bitmap mLocalBitmap;

    private boolean mShowLocal;
    private File imageFile = null;
    private String imgUrl;

    private Drawable drawable;

    private ImageButton deleteImgBtn;
    private FormFieldInfo formFieldInfo;

    public CustomNetworkImageView(Context context) {
        this(context, null);
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageUrl(String url, ImageLoader imageLoader) {
        mShowLocal = false;

        if (url != null && url.startsWith("http")) {
            this.imgUrl=url;
            imageLoader.get(url, ImageLoader.getImageListener(
                    this, R.mipmap.no_img, R.mipmap.no_img));
        }

        super.setImageUrl(url, imageLoader);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        super.onLayout(changed, left, top, right, bottom);
        if (mShowLocal) {

            if (mLocalBitmap != null)
                setImageBitmap(mLocalBitmap);
            else {
                setImageDrawable(drawable);
            }

        }
    }

    public void setLocalImageBitmap(Bitmap unscaledBitmap) {
        if (unscaledBitmap != null) {
            mShowLocal = true;
            if (deleteImgBtn != null)
                deleteImgBtn.setVisibility(VISIBLE);
        } else {
            if (deleteImgBtn != null)
                deleteImgBtn.setVisibility(GONE);
        }

//        // Part 2: Scale image
//        assert unscaledBitmap != null;
//        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, appUtils.convertDpToPixel(100),
//                appUtils.convertDpToPixel(100), ScalingUtilities.ScalingLogic.FIT);
//        unscaledBitmap.recycle();
        setScaleType(ScaleType.FIT_CENTER);

        this.mLocalBitmap = unscaledBitmap;
        requestLayout();
    }

    public void setDrawable(int drawable) {
        if (drawable != 0) {
            mShowLocal = true;
        }
        this.drawable = getResources().getDrawable(drawable);

        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),
                drawable);

        setLocalImageBitmap(icon);
        requestLayout();
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setFileBitmap(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        setLocalImageBitmap(bitmap);
        setImageFile(new File(filePath));

    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public ImageButton getDeleteImgBtn() {
        return deleteImgBtn;
    }

    public void setDeleteImgBtn(ImageButton deleteImgBtn) {
        this.deleteImgBtn = deleteImgBtn;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public FormFieldInfo getFormFieldInfo() {
        return formFieldInfo;
    }

    public void setFormFieldInfo(FormFieldInfo formFieldInfo) {
        this.formFieldInfo = formFieldInfo;
    }
}