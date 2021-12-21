/*
 * Copyright (c) 2018. All Rights Reserved by Punjab Food Authority
 */

package com.pfa.pfaapp.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pfa.pfaapp.AppController;
import com.pfa.pfaapp.BaseActivity;
import com.pfa.pfaapp.ImageGalleryActivity;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.models.PFATableInfo;
import com.pfa.pfaapp.utils.AppUtils;

import java.util.List;

import static com.pfa.pfaapp.utils.AppConst.EXTRA_DOWNLOAD_URL;

public class ImagesGridAdapter extends BaseAdapter {
    private final BaseActivity baseActivity;
    private final List<PFATableInfo>  imagesList;

    public ImagesGridAdapter(BaseActivity baseActivity, List<PFATableInfo>  imagesList) {
        this.baseActivity = baseActivity;
        this.imagesList = imagesList;
    }

    @Override
    public int getCount() {
        return imagesList.size();
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
            holder.fboMenuNameTV.setVisibility(View.GONE);
            holder.fbo_grid_ll = convertView.findViewById(R.id.fbo_grid_ll);
            holder.deleteImgBtn = convertView.findViewById(R.id.deleteImgBtn);
            holder.deleteImgBtn.setVisibility(View.GONE);

            baseActivity.sharedPrefUtils.applyFont(holder.fboMenuNameTV, AppUtils.FONTS.HelveticaNeueMedium);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (imagesList.get(position).getData().startsWith("http"))
            holder.mediaGridNIV.setImageUrl(imagesList.get(position).getData(), AppController.getInstance().getImageLoader());
        else {
            holder.mediaGridNIV.setImageResource(R.mipmap.no_img);
        }

        holder.mediaGridNIV.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_DOWNLOAD_URL,imagesList.get(position).getData());
            baseActivity.sharedPrefUtils.startNewActivity(ImageGalleryActivity.class,bundle,false);

        });
        return convertView;
    }

    static class ViewHolder {
        CustomNetworkImageView mediaGridNIV;
        TextView fboMenuNameTV;
        RelativeLayout fbo_grid_ll;
        ImageButton deleteImgBtn;
    }
}
