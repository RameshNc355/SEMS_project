package com.project.android.secureexammanagementsystem.proctor;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.ImageAdapter;
import com.project.android.secureexammanagementsystem.utility.Photo;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetPhotosFragment extends Fragment {

    GridView gvPhotos;
    TextView tvNoPhotos;
    ProgressBar progressBar;
    int student_id;
    int exam_id;
    List<Photo> photos;
    public GetPhotosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_photos, container, false);
        gvPhotos = (GridView)view.findViewById(R.id.gvPhotos);
        tvNoPhotos = (TextView)view.findViewById(R.id.tvNoPhotos);
        progressBar = (ProgressBar)view.findViewById(R.id.pbPhotos);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        student_id = getArguments().getInt(getString(R.string.student_id_argument));
        exam_id = getArguments().getInt(getString(R.string.exam_id_param));
        loadPhotos();
    }

    private void loadPhotos() {
        SEMSRestClient client = new SEMSRestClient();
        String url="/photos/list";
        RequestParams params = new RequestParams();
        params.put(getString(R.string.student_id_argument),student_id);
        params.put(getString(R.string.exam_id_param), exam_id);

        progressBar.setVisibility(View.VISIBLE);
        client.get(url,params ,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.print(responseBody);
                tvNoPhotos.setText("");
                progressBar.setVisibility(View.GONE);
                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONObject obj = new JSONObject(response);
                    JSONArray arr = obj.getJSONArray("list");
                    photos = new ArrayList<Photo>();
                    ImageAdapter photo_adapter = new ImageAdapter(getContext(), R.layout.photo_item, (ArrayList<Photo>) photos);
                    for(int i=0; i< arr.length(); i++){
                        JSONObject photoObj = arr.getJSONObject(i);
                        photos.add(new Photo(photoObj.getInt("_id"),photoObj.getString("photo"),photoObj.getString("time")));

                    }
                    gvPhotos.setAdapter(photo_adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tvNoPhotos.setText(getString(R.string.no_photos_available_text));
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
