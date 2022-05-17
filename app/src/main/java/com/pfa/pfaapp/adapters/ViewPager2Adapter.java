package com.pfa.pfaapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pfa.pfaapp.AppController;
import com.pfa.pfaapp.R;
import com.pfa.pfaapp.customviews.CustomNetworkImageView;
import com.pfa.pfaapp.pinchzoom.PinchZoomPhotoView;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Adapter extends RecyclerView.Adapter<ViewPager2Adapter.ViewHolder> {

    // Array of images
    // Adding images from drawable folder
    List<String> imagesList;
    String postion1;
//    private int[] images = {R.drawable.doc_large, R.drawable.pdf_large};
    private Context ctx;

    // Constructor of our ViewPager2Adapter class
    public ViewPager2Adapter(Context ctx, List<String> imagesList , String postion) {
        this.ctx = ctx;
        this.imagesList = imagesList;
        this.postion1 = postion;
    }

    // This method returns our layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.viewpager_gallery_row, parent, false);
        return new ViewHolder(view);
    }

    // This method binds the screen with the view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // This will set the images in imageview
        Log.d("ImageListSize" , "size in viewpager= " + imagesList.size());
        Log.d("ImageListSize" , "image in viewpager= " + imagesList.get(position));
        holder.images.setImageUrl(imagesList.get(Integer.parseInt(postion1)) , AppController.getInstance().getImageLoader());
//        holder.images.setImageUrl(imagesList.get(position) , );
    }

    // This Method returns the size of the Array
    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    // The ViewHolder class holds the view
    public static class ViewHolder extends RecyclerView.ViewHolder {
        PinchZoomPhotoView images;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            images = itemView.findViewById(R.id.images);
        }
    }
}
