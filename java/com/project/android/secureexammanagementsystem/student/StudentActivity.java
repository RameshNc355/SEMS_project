package com.project.android.secureexammanagementsystem.student;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.LoginActivity;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class StudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GetExamsFragment.TakeExamListener, TakeExamFragment.GetResultListener {
    int student_id;
    int exam_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_student);
        setSupportActionBar(toolbar);
        if(getIntent().hasExtra("id")) {
            student_id = getIntent().getIntExtra("id", -1);
        }else{
            Toast.makeText(StudentActivity.this, getString(R.string.invalid_user_text), Toast.LENGTH_SHORT).show();
            return;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;

        if (id == R.id.nav_book_exam) {
            fragment = new BookExamFragment();
        } else if (id == R.id.nav_take_exam) {
            fragment = new GetExamsFragment();
        } else if (id == R.id.nav_student_logout) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.student_id_argument),student_id);
        fragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.fl_student, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void takeExam(int exam_id, int student_id, String course) {
        this.exam_id = exam_id;
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new TakeExamFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.student_id_argument),student_id);
        bundle.putInt("exam_id",exam_id);
        bundle.putString("course", course);
        fragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.fl_student, fragment).commit();

    }
    public void getResult(int score, String course){
        GetResultFragment fragment = new GetResultFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(getString(R.string.student_id_argument), student_id);
        bundle.putInt("score", score);
        bundle.putString("course", course);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_student, fragment).commit();
    }
}
