package com.project.android.secureexammanagementsystem.proctor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.project.android.secureexammanagementsystem.LoginActivity;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.Student;

public class ProctorActivity extends AppCompatActivity implements ExamStudentFragment.StudentViewListener {

    Switch swVerified;
    String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_proctor);
        setSupportActionBar(toolbar);
        swVerified = (Switch)findViewById(R.id.swVerified);
        swVerified.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    status = "YES";
                }else{
                    status = "NO";
                }
                loadStudents(status);
            }
        });
        loadStudents("NO");
    }
    private void loadStudents(String status){
        ExamStudentFragment fragment = new ExamStudentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.status_param),status);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_proctor,fragment).commit();
    }

    @Override
    public void viewStudentPhoto(Student student, String currentStatus) {
        if(currentStatus.equals("NO")) {
            AuthenticationFragment fragment = new AuthenticationFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("student_id", student._id);
            bundle.putString("photo", student.photo);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_proctor, fragment).addToBackStack(null).commit();
        }else {
            GetPhotosFragment fragment = new GetPhotosFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("student_id", student._id);
            bundle.putInt("exam_id",student.exam_id);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_proctor, fragment).addToBackStack(null).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.proctor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.nav_proctor_logout){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
