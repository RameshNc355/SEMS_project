package com.project.android.secureexammanagementsystem.admin;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.Helper;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;

/*import org.apache.http.entity.mime.MultipartEntity;*/

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddUserFragment extends Fragment {

    private static final int TAKE_PICTURE = 1001;
    private static final int REQUEST_PERMISSION = 1008;
    Spinner spUserType;
    EditText etUserName, etEmail, etPassword;
    ImageView ivPhoto;
    Button btnTakePhoto, btnAddUser;
    String response;
    Uri outputFileUri;
    File file;
    String[] levels = {"Student","Teacher"};
    public AddUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_add_user, container, false);
        etUserName = (EditText)view.findViewById(R.id.etUserName);
        etEmail = (EditText)view.findViewById(R.id.etEmail);
        etPassword = (EditText)view.findViewById(R.id.etUserPassword);
        spUserType = (Spinner)view.findViewById(R.id.spUserType);
        ivPhoto = (ImageView)view.findViewById(R.id.ivPhoto);
        btnTakePhoto = (Button)view.findViewById(R.id.btnTakePhoto);
        btnAddUser = (Button)view.findViewById(R.id.btn_add_user);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.user_levels, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUserType.setAdapter(adapter);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM),"photo.jpeg");
                outputFileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });

    }

    private void addUser() {
        final String name = etUserName.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String type = spUserType.getSelectedItem().toString();

        if(Helper.isNotNull(name) && Helper.isNotNull(email) && Helper.isNotNull(password) && Helper.isNotNull(type)) {
            if(Helper.validate(email)) {
                final SEMSRestClient client = new SEMSRestClient();


                if (Build.VERSION.SDK_INT >= 23) {
                    if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION);
                    }
                }
                // Step 1 : upload the photo first
                try {
                    if(file == null)
                        return;

                    String url = "/upload/file";
                    RequestParams params = new RequestParams();
                    InputStream is = new FileInputStream(file);

                    params.put("file", is);
                    params.put("name", "profilePhoto.jpg");
                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            System.out.println("Success");
                            if (responseBody != null) {
                                String response = null;
                                try {
                                    response = new String(responseBody, "utf-8");
                                    // JSON Object
                                    JSONObject obj = new JSONObject(response);
                                    // When the JSON response has status boolean value assigned with true
                                    if(obj.getBoolean("status")) {

                                        String uploadfile_addr = obj.getString("error_msg");
                                        // step 2 :  insert the record with uploaded path address
                                        String url = "/insert/user";

                                        RequestParams params = new RequestParams();
                                        params.put(getString(R.string.name_param), name);
                                        params.put(getString(R.string.email_param), email);
                                        params.put(getString(R.string.password_param), password);
                                        params.put(getString(R.string.type_param), type);
                                        params.put("photo", uploadfile_addr);

//                        params.setHttpEntityIsRepeatable(true);

                                        client.post(url, params, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                System.out.print(responseBody);
                                                Toast.makeText(getContext(), getString(R.string.insert_user_success_text), Toast.LENGTH_LONG).show();
                                                etEmail.setText("");
                                                etUserName.setText("");
                                                etPassword.setText("");
                                                spUserType.setSelection(0);
                                                ivPhoto.setImageResource(R.drawable.icon_person);
                                                return;
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                Toast.makeText(getContext(), getString(R.string.insert_user_failed_text), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if(responseBody != null)
                                System.out.println(new String(responseBody, StandardCharsets.UTF_8));
                            System.out.println(error.getMessage());
                            System.out.println("Failure");
                        }
                    });

                } catch (FileNotFoundException e) {
                    Toast.makeText(getContext(), getString(R.string.kindly_take_a_photo_text), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getContext(), getString(R.string.enter_valid_email_text), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getContext(), getString(R.string.enter_all_fields_text), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION ){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addUser();

            } else {

                Toast.makeText(getContext(), getString(R.string.no_permission_text), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    Bitmap photo = null;
                    if (data != null) {
                        if (data.hasExtra("data")) {
                            outputFileUri = data.getData();
                           /* photo = (Bitmap) data.getExtras().get("data");
                            ivPhoto.setImageBitmap(photo);*/
                        }
                    } else {
                       /* photo = BitmapFactory.decodeFile(outputFileUri.getPath());
                        photo = Bitmap.createScaledBitmap(photo, ivPhoto.getWidth(), ivPhoto.getHeight(), false);
                        ivPhoto.setImageBitmap(photo);*/
                    }
                    String imagepath = outputFileUri.getPath();
                    Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                    ivPhoto.setImageBitmap(scaledBitmap);

                    File myDir=new File( Environment.getExternalStorageDirectory(), getContext().getPackageName());
                    if(!myDir.exists()){
                        myDir.mkdir();
                    }
                    String fname = "UploadedImage.png";
                    file = new File (myDir, fname);
                    if (file.exists ())
                        file.delete ();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        int compressFactor = 70;
                        do {
                            out.flush();
                            file.createNewFile();
//                            out = new FileOutputStream(file);
                            scaledBitmap.compress(Bitmap.CompressFormat.PNG, compressFactor, out);
                            compressFactor-=10;
                        }while(file.length()>65535);// 65535- Blob data size
                        out.flush();
                        out.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

            }
        }
    }
}
