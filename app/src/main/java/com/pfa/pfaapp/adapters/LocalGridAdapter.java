/*
 * Copyright (c) 2018. All Rights Reserved by Punjab Food Authority
 */

package com.pfa.pfaapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pfa.pfaapp.AppController;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.ImageGalleryActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.interfaces.WhichItemClicked;
import com.pfa.pfaapp.models.FormSectionInfo;
import com.pfa.pfaapp.utils.AppUtils;

import java.io.File;
import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_DOWNLOAD_URL;

public class LocalGridAdapter extends BaseAdapter {
    private final BaseActivity baseActivity;

    private List<FormSectionInfo> formSectionInfos;

    private final WhichItemClicked whichItemClicked;

    public LocalGridAdapter(BaseActivity baseActivity, List<FormSectionInfo> formSectionInfos, WhichItemClicked whichItemClicked) {
        this.baseActivity = baseActivity;
        this.whichItemClicked = whichItemClicked;
        this.formSectionInfos = formSectionInfos;
        Log.d("viewCreated", "LocalGridAdapter");
    }

    public void updateAdapter(List<FormSectionInfo> formSectionInfos) {
        this.formSectionInfos = formSectionInfos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return formSectionInfos.size();
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
            convertView = inflater.inflate(R.layout.local_media_grid_item, parent, false);
            holder = new ViewHolder();
            holder.mediaGridNIV = convertView.findViewById(R.id.mediaGridNIV);

            holder.fboMenuNameTV = convertView.findViewById(R.id.fboMenuNameTV);
            holder.fbo_grid_ll = convertView.findViewById(R.id.fbo_grid_ll);
            holder.deleteImgBtn = convertView.findViewById(R.id.deleteImgBtn);
            holder.vidImg = convertView.findViewById(R.id.vidImg);

            baseActivity.sharedPrefUtils.applyFont(holder.fboMenuNameTV, AppUtils.FONTS.HelveticaNeueMedium);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.vidImg.setVisibility(View.GONE);
        try {
            ///////// Check if video or image
            String filePath = formSectionInfos.get(position).getFields().get(0).getData().get(0).getValue();

            if (baseActivity.sharedPrefUtils.isVideoFile(filePath)) {
                holder.vidImg.setVisibility(View.VISIBLE);
                if (filePath.startsWith("http")) {
                    baseActivity.sharedPrefUtils.setVideoThumbFromUlr(holder.mediaGridNIV, filePath);
                } else {
                    baseActivity.sharedPrefUtils.setVideoThumb(holder.mediaGridNIV, filePath);
                }
            } else {
                if (filePath.startsWith("http")) {
                    holder.mediaGridNIV.setImageUrl(filePath, AppController.getInstance().getImageLoader());
                } else {
                    File imgFile = new File(filePath);

                    if (imgFile.exists()) {

                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

//                        holder.mediaGridNIV.setFileBitmap(filePath);
                        holder.mediaGridNIV.setLocalImageBitmap(myBitmap);
                    }
                }

                Glide.with(baseActivity).load(filePath).into(holder.mediaGridNIV);
            }

            holder.mediaGridNIV.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString(EXTRA_DOWNLOAD_URL, formSectionInfos.get(position).getFields().get(0).getData().get(0).getValue());
                baseActivity.sharedPrefUtils.startNewActivity(ImageGalleryActivity.class, bundle, false);

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (formSectionInfos.get(position).getFields() != null && formSectionInfos.get(position).getFields().get(0).isNotEditable()) {
            holder.deleteImgBtn.setVisibility(View.GONE);
        }

        holder.deleteImgBtn.setOnClickListener(view -> baseActivity.sharedPrefUtils.
                showTwoBtnsMsgDialog("Are you sure you want to delete?", message -> {
                    if (message != null && message.isEmpty()) {
                        baseActivity.sharedPrefUtils.printLog("Grid Image Url: ", "" + (formSectionInfos.get(position).getSection_name()));// .getFields().get(0).getData().get(0).getValue()));
                        whichItemClicked.whichItemClicked("" + position);
                    }
                }));

        return convertView;
    }

    static class ViewHolder {
        CustomNetworkImageView mediaGridNIV;
        TextView fboMenuNameTV;
        RelativeLayout fbo_grid_ll;
        ImageButton deleteImgBtn;
        ImageView vidImg;
    }
}
