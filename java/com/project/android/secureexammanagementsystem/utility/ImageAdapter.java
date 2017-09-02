package com.project.android.secureexammanagementsystem.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.android.secureexammanagementsystem.R;

import java.util.ArrayList;

/**
 * Created by vishakha on 05-03-2017.
 */
public class ImageAdapter extends ArrayAdapter<Photo> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Photo> photos = new ArrayList<Photo>();

    public ImageAdapter(Context context, int layoutResourceId, ArrayList<Photo> photos) {
        super(context, layoutResourceId, photos);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.photos = photos;
    }


    /**
     * Updates grid data and refresh grid items.
     * @param photos
     */
    public void setGridData(ArrayList<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.tvGridTime = (TextView) row.findViewById(R.id.tvGridTime);
            holder.ivGridPhoto = (ImageView) row.findViewById(R.id.ivGridPhoto);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Photo item = photos.get(position);
        holder.tvGridTime.setText(item.time);
        Bitmap bitmap = StringToBitMap(item.photo);
        if(bitmap!=null) {
            holder.ivGridPhoto.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
        }else{
            holder.ivGridPhoto.setImageResource(R.drawable.icon_person);
        }

        return row;
    }

    static class ViewHolder {
        TextView tvGridTime;
        ImageView ivGridPhoto;
    }
    public Bitmap StringToBitMap(String image){
        try{

            byte [] encodeByte= Base64.decode(image,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
    /*// create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

//        ivGridPhoto.setImageURI();(position]);
        return imageView;
    }


}*/