package com.project.android.secureexammanagementsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.admin.AdminActivity;
import com.project.android.secureexammanagementsystem.proctor.ProctorActivity;
import com.project.android.secureexammanagementsystem.student.StudentActivity;
import com.project.android.secureexammanagementsystem.utility.Helper;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    EditText etUserID, etPassword;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUserID = (EditText)findViewById(R.id.etUserID);
        etPassword = (EditText)findViewById(R.id.etPassword);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

    }

    public void login(View view) {
        String email = etUserID.getText().toString();
        String password = etPassword.getText().toString();

        RequestParams params = new RequestParams();
        if(Helper.isNotNull(email) && Helper.isNotNull(password)){
            // When Email entered is Valid
            if(Helper.validate(email)){
                params.put("username", email);
                params.put("password", password);
                // Invoke RESTful Web Service with Http parameters
                invokeWS(params);
            }
            // When Email is invalid
            else{
                Toast.makeText(getApplicationContext(), getString(R.string.enter_valid_email_text), Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(getApplicationContext(), getString(R.string.enter_all_fields_text), Toast.LENGTH_LONG).show();
        }

    }
    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        progressDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        SEMSRestClient client = new SEMSRestClient();
        client.get("/login/dologin",params ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                progressDialog.hide();
                progressDialog.dismiss();
                try {
                    String response = new String(responseBody, "UTF-8");
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    if(obj.getBoolean("status")){
                        Toast.makeText(getApplicationContext(), getString(R.string.logged_in_text), Toast.LENGTH_LONG).show();
                        int id = obj.getInt("id");
                        String type = obj.getString("type");
                        Intent intent = null;
                        if(type.equals("Student")){
                            intent = new Intent(LoginActivity.this, StudentActivity.class);
                            intent.putExtra("id", id);

                        }else if(type.equals("Teacher")){
                            intent = new Intent(LoginActivity.this, ProctorActivity.class);

                        }else if(type.equals("admin")){
                            intent = new Intent(LoginActivity.this, AdminActivity.class);
                        }
                        startActivity(intent);
                        etPassword.setText("");
                        etUserID.setText("");
                    }
                    // Else display error message
                    else{

                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_json_text), Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(getApplicationContext(),  getString(R.string.invalid_json_text), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                progressDialog.hide();
                progressDialog.dismiss();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), getString(R.string.requested_resource_not_found_text), Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}
