package com.project.android.secureexammanagementsystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.project.android.secureexammanagementsystem.admin.AdminActivity;
import com.project.android.secureexammanagementsystem.proctor.ProctorActivity;
import com.project.android.secureexammanagementsystem.student.StudentActivity;

public class DummyNavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy_navigation);
    }

    public void navigate(View view) {
        Intent intent = null;
        if(view == findViewById(R.id.btnAdmin)){
            intent = new Intent(this, AdminActivity.class);
        }
        else if(view == findViewById(R.id.btnProctor)){
            intent = new Intent(this, ProctorActivity.class);
        }
        else if(view == findViewById(R.id.btnStudent)){
            intent = new Intent(this, StudentActivity.class);
        }
        startActivity(intent);
    }
}
