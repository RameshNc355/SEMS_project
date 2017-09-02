package com.project.android.secureexammanagementsystem.student;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;

import junit.runner.Version;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;

public class CameraService extends Service {

    int student_id;
    int exam_id;
    Activity activity;
    boolean isCameraConnected =false, isPreviewStarted = false, hasCamera;
    Camera camera;
    int cameraId;
    static Thread t;

    private static final int REQUEST_PERMISSION = 1000;

    public CameraService() {


    }

    public class MyBinder extends Binder {
        CameraService getService(){
            return CameraService.this;
        }
    }
    private final IBinder binder = new MyBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("BinderService","in onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    public void takePhoto(final Activity activity, final int student_id, final int exam_id){
        this.student_id = student_id;
        this.exam_id = exam_id;
        this.activity = activity;

        t = performOnBackgroundThread(new Runnable() {
            @Override
            public void run() {
                capturePhoto();
            }
        });
            }
    public static Thread performOnBackgroundThread(final Runnable runnable) {
        t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    private void capturePhoto(){

        File directory = new File(Environment.getExternalStorageDirectory() + "/sems/");

        if (!directory.exists()) {
            directory.mkdir();
        }
        final File file = new File (directory, "pic1.jpeg");

        Context context = getApplicationContext();

        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            cameraId = getFrontCameraId();

            if(cameraId != -1){
                hasCamera = true;
            }else{
                hasCamera = false;
            }
        }else{
            hasCamera = false;
        }

        if(hasCamera) {
            if (!isCameraConnected) {
                if(camera!=null){ camera.release();}
                camera = Camera.open(cameraId);
                try {
                    camera.setPreviewTexture(new SurfaceTexture(10));
                } catch (IOException e1) {
                    Log.e("CAMERA", e1.getMessage());
                }

                Camera.Parameters params = camera.getParameters();
                params.setPreviewSize(640, 480);

                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                params.setPictureFormat(ImageFormat.JPEG);
                camera.setParameters(params);
                isCameraConnected = true;
            }
            if (!isPreviewStarted) {
                isPreviewStarted = true;
                camera.startPreview();
                camera.takePicture(null, null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Log.i("CAMERA", "picture-taken");
                        try {
                            Bitmap bitmap= BitmapFactory.decodeByteArray(data, 0, data.length);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);

                            FileOutputStream out = new FileOutputStream(file);
                            int compressFactor = 70;
                            do {
                                out.flush();
                                file.createNewFile();

                                scaledBitmap.compress(Bitmap.CompressFormat.PNG, compressFactor, out);
                                compressFactor-=10;
                            }while(file.length()>65535);// 65535- Blob data size
                            out.flush();
                            out.close();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        loadPicture(file);
                        if (isPreviewStarted) {
                            isPreviewStarted = false;
                            camera.stopPreview();
                        }
                        if (isCameraConnected) {
                            isCameraConnected = false;
                            camera.release();
                        }
                        t.interrupt();
                    }
                });
            }

        }

    }
    private int getFrontCameraId(){
        int camId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo ci = new Camera.CameraInfo();

        for(int i = 0;i < numberOfCameras;i++){
            Camera.getCameraInfo(i,ci);
            if(ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                camId = i;
            }
        }

        return camId;
    }
    private void loadPicture( File file) {
        final SEMSRestClient client = new SEMSRestClient();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
                            if (obj.getBoolean("status")) {

                                String uploadfile_addr = obj.getString("error_msg");


                                String url = "/insert/photos";

                                RequestParams params = new RequestParams();
                                params.put(getString(R.string.exam_id_param), exam_id);
                                params.put(getString(R.string.student_id_param), student_id);

                                params.put("photo", uploadfile_addr);

                                client.post(url, params, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        System.out.print(responseBody);
                                        Log.i(getClass().getSimpleName(), getString(R.string.insert_user_success_text));
                                        return;
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Log.i(getClass().getSimpleName(), getString(R.string.insert_user_failed_text));
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
                    Log.i(getClass().getSimpleName(),error.getMessage());
                    System.out.println("Failure");
                }
            });

        } catch (FileNotFoundException e) {
            Log.i(getClass().getSimpleName(), getString(R.string.kindly_take_a_photo_text));
        }
    }
}
