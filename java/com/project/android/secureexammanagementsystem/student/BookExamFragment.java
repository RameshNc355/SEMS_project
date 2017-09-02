package com.project.android.secureexammanagementsystem.student;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.Helper;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookExamFragment extends Fragment {
    Spinner spSubject, spLevel;
    Button btnBookExam;
    ArrayList subjects;
    ArrayAdapter<String> subject_adapter;

    public BookExamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_exam, container, false);
        spSubject = (Spinner)view.findViewById(R.id.spSubject);
        spLevel = (Spinner)view.findViewById(R.id.spExamLevel);
        btnBookExam = (Button)view.findViewById(R.id.btn_book_exam);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final int student_id = getArguments().getInt(getString(R.string.student_id_argument), -1);
        if(student_id == -1){
            Toast.makeText(getContext(), getString(R.string.please_login_text), Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayAdapter<CharSequence> level_adapter = ArrayAdapter.createFromResource(getContext(), R.array.question_levels,android.R.layout.simple_spinner_item);
        level_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLevel.setAdapter(level_adapter);

        subjects = new ArrayList();
        subject_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, subjects);
        subject_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(subject_adapter);
        loadSubjects();

        btnBookExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = spSubject.getSelectedItem().toString();
                String level = spLevel.getSelectedItem().toString();

                if (Helper.isNotNull(level) && Helper.isNotNull(subject)) {


                    SEMSRestClient client = new SEMSRestClient();

                    String url = "/insert/exam";
                    RequestParams params = new RequestParams();

                    params.put(getString(R.string.student_id_param), student_id);
                    params.put(getString(R.string.level_param), level);
                    params.put(getString(R.string.subject_param), subject);

                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            System.out.print(responseBody);
                            try {
                                String response = new String(responseBody, "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getContext(), getString(R.string.insert_exam_success_text), Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getContext(), getString(R.string.unable_to_create_exam_text), Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                } else {
                    Toast.makeText(getContext(), getString(R.string.enter_all_fields_text), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }
    private void loadSubjects() {
        SEMSRestClient client = new SEMSRestClient();
        String url="/subjects/list";

        client.get(url,null ,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.print(responseBody);

                try {
                    String response = new String(responseBody, "UTF-8");
                    JSONObject obj = new JSONObject(response);
                    JSONArray arr = obj.getJSONArray("list");

                    for(int i=0; i< arr.length(); i++){
                        subject_adapter.add(arr.getString(i));
                    }
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

}
