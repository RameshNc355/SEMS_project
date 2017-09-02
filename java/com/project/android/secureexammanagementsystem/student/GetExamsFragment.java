package com.project.android.secureexammanagementsystem.student;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.R;
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
public class GetExamsFragment extends Fragment {

    ListView lv_available_exams;
    TextView tvNoAvailableExams;
    List exams;
    ArrayAdapter exam_adapter;
    int[] exam_ids;
    int student_id;

    TakeExamListener listener;

    public GetExamsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (TakeExamListener)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_exams, container, false);
        lv_available_exams = (ListView)view.findViewById(R.id.lv_available_exams);
        tvNoAvailableExams = (TextView)view.findViewById(R.id.tvNoAvailableExams);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        student_id = getArguments().getInt(getString(R.string.student_id_argument));
        exams = new ArrayList();
        exam_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, exams);

        lv_available_exams.setAdapter(exam_adapter);
        lv_available_exams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String exam = exam_adapter.getItem(i).toString();
                String course = exam.substring(0,exam.indexOf(":::"));
                listener.takeExam(exam_ids[i],student_id, course);
            }
        });
        loadExams();

    }
    private void loadExams(){
        SEMSRestClient client = new SEMSRestClient();
        String url="/exams/list";
        RequestParams params = new RequestParams();
        params.put(getString(R.string.student_id_argument),student_id);

        client.get(url,null ,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                System.out.print(responseBody);
                if(isAdded()) {
                    tvNoAvailableExams.setText("");
                    try {
                        String response = new String(responseBody, "UTF-8");
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("list");
                        exam_ids = new int[arr.length()];
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject examObj = arr.getJSONObject(i);
                            exam_adapter.add(examObj.getString("course") + ":::" + examObj.getString("level"));
                            exam_ids[i] = examObj.getInt("exam_id");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if(isAdded()) {
                    tvNoAvailableExams.setText(getString(R.string.no_available_exams_text));
                }
            }
        });

    }

    public interface TakeExamListener{
        void takeExam(int exam_id, int student_id, String course);
    }
}
