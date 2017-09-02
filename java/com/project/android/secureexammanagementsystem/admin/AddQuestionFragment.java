package com.project.android.secureexammanagementsystem.admin;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
public class AddQuestionFragment extends Fragment {

    EditText etQuestion, etAns1, etAns2, etAns3, etAns4, etCorrectAns;
    Spinner spLevel, spSubject;
    Button btnAddQuestion;
    ArrayList subjects;
    ArrayAdapter<String> subject_adapter;

    public AddQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_add_question, container, false);
        etQuestion = (EditText)view.findViewById(R.id.etQuestion);
        etAns1 = (EditText)view.findViewById(R.id.etAnswer1);
        etAns2 = (EditText)view.findViewById(R.id.etAnswer2);
        etAns3 = (EditText)view.findViewById(R.id.etAnswer3);
        etAns4 = (EditText)view.findViewById(R.id.etAnswer4);
        etCorrectAns = (EditText)view.findViewById(R.id.etCorrectAnswer);
        spLevel = (Spinner)view.findViewById(R.id.spLevel);
        spSubject = (Spinner)view.findViewById(R.id.spQueSubject);
        btnAddQuestion = (Button)view.findViewById(R.id.btn_add_question);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<CharSequence> level_adapter = ArrayAdapter.createFromResource(getContext(), R.array.question_levels,android.R.layout.simple_spinner_item);
        level_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLevel.setAdapter(level_adapter);

        subjects = new ArrayList();
        subject_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, subjects);
        subject_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(subject_adapter);
        loadSubjects();
        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = etQuestion.getText().toString();
                String ans1 = etAns1.getText().toString();
                String ans2 = etAns2.getText().toString();
                String ans3 = etAns3.getText().toString();
                String ans4 = etAns4.getText().toString();
                String correct_ans = etCorrectAns.getText().toString();
                String level = spLevel.getSelectedItem().toString();
                String course = spSubject.getSelectedItem().toString();
                if (Helper.isNotNull(question) && Helper.isNotNull(ans1) && Helper.isNotNull(ans2) && Helper.isNotNull(ans3) && Helper.isNotNull(ans4)
                        && Helper.isNotNull(correct_ans) && Helper.isNotNull(level) && Helper.isNotNull(course)) {

                    SEMSRestClient client = new SEMSRestClient();

                    String url = "/insert/question";
                    RequestParams params = new RequestParams();
                    params.put(getString(R.string.question_param), question);
                    params.put(getString(R.string.ans1_param), ans1);
                    params.put(getString(R.string.ans2_param), ans2);
                    params.put(getString(R.string.ans3_param), ans3);
                    params.put(getString(R.string.ans4_param), ans4);
                    params.put(getString(R.string.correct_ans_param), correct_ans);
                    params.put(getString(R.string.level_param), level);
                    params.put(getString(R.string.subject_param), course);

                    client.post(url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            System.out.print(responseBody);
                            String response = responseBody.toString();
                            Toast.makeText(getContext(), getString(R.string.insert_question_success_text), Toast.LENGTH_LONG).show();
                            etQuestion.setText("");
                            etAns1.setText("");
                            etAns2.setText("");
                            etAns3.setText("");
                            etAns4.setText("");
                            etCorrectAns.setText("");

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getContext(), getString(R.string.insert_question_failed_text), Toast.LENGTH_LONG).show();
                        }
                    });
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
