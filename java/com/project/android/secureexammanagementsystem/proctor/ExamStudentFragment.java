package com.project.android.secureexammanagementsystem.proctor;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;
import com.project.android.secureexammanagementsystem.utility.Student;
import com.project.android.secureexammanagementsystem.utility.StudentArrayAdapter;

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
public class ExamStudentFragment extends Fragment {

    StudentViewListener listener;
    ListView lvExamStudents;
    StudentArrayAdapter student_adapter;
    List<Student> students;
    String currentStatus;

    public ExamStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        currentStatus = getArguments().getString(getString(R.string.status_param));

            if (context instanceof StudentViewListener)
                listener = (StudentViewListener) context;
            else
                throw new RuntimeException("Need to implement the StudentViewListener");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_student, container, false);
        lvExamStudents = (ListView)view.findViewById(R.id.lv_exam_students);
        View header = (View)inflater.inflate(R.layout.student_list_header, null);
        lvExamStudents.addHeaderView(header);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        students = new ArrayList<>();
        loadStudents();

    }

    private void loadStudents() {
        SEMSRestClient client = new SEMSRestClient();
        String url="/students/list";

        RequestParams params = new RequestParams();
        params.put("status", currentStatus);

        client.get(url,params ,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.print(responseBody);

                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONObject obj = new JSONObject(response);
                    JSONArray arr = obj.getJSONArray("list");

                    for(int i=0; i< arr.length(); i++){
                        JSONObject stud = arr.getJSONObject(i);


                        Student student = new Student(stud.getInt("_id"), stud.getInt("exam_id"), stud.getString("name"), stud.getString("encodedPhoto"), stud.getString("course"), stud.getString("level"));
                        Log.d("STUDENT", student.toString());
                        students.add(student);
                    }
                    student_adapter = new StudentArrayAdapter(getContext(), R.layout.student_info, students);
                    lvExamStudents.setAdapter(student_adapter);
                    lvExamStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Student s = student_adapter.getItem(i);
                                listener.viewStudentPhoto(s, currentStatus);

                        }
                    });

                    registerForContextMenu(lvExamStudents);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("SubjectsLoader", error.getMessage());
            }
        });

    }

    public interface StudentViewListener {
        void viewStudentPhoto(Student student, String currentStatus);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(currentStatus.equals("NO")) {
            menu.add(Menu.NONE, R.id.menu_student_verify, Menu.NONE, getString(R.string.verify_status));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_student_verify:
                verify_student(info.position);
                return true;

        }
        return super.onContextItemSelected(item);
    }

    private void verify_student(int position) {
        SEMSRestClient client = new SEMSRestClient();

        String url="/update/status";
        RequestParams params = new RequestParams();
        final Student student = (Student)(student_adapter.getItem(position));
        params.put(getString(R.string.exam_id_param),student.exam_id);
        params.put(getString(R.string.status_param),"YES");

        client.post(url,params,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.print(responseBody);
                try {
                    String response = new String(responseBody, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(),getString(R.string.student_verified_successfully_text),Toast.LENGTH_LONG).show();
//                student_adapter.remove(student);
                students.remove(student);
                student_adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),getString(R.string.unable_to_verify_student_text),Toast.LENGTH_LONG).show();
            }
        });

    }
}
