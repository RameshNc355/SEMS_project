package com.project.android.secureexammanagementsystem.proctor;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class AuthenticationFragment extends Fragment {

    ImageView ivPhoto;
    public AuthenticationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);
        ivPhoto = (ImageView)view.findViewById(R.id.ivStudentPhoto);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getProfilePhoto();
    }

    private void getProfilePhoto() {
        int id = getArguments().getInt("student_id");
        String photo = getArguments().getString("photo");
        Bitmap bitmap = StringToBitMap(photo);//BitmapFactory.decodeByteArray(photo,0,photo.length);//

        if(bitmap!=null) {
            ivPhoto.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
        }else{
            ivPhoto.setImageResource(R.drawable.icon_person);
        }

    }
    public static Bitmap StringToBitMap(String image){
        try{

            byte [] encodeByte= Base64.decode(image,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}
