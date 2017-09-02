package com.project.android.secureexammanagementsystem.student;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.LoginActivity;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.Question;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;
import com.project.android.secureexammanagementsystem.utility.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class TakeExamFragment extends Fragment {

    private static final String CORRECT = "C";
    private static final String WRONG = "W";
    TextView tvQuestion;
    RadioButton rbAns1, rbAns2, rbAns3, rbAns4;
    RadioGroup rgAnswers;
    Button btnNext;
    ArrayList<Question> questions;
    int currentQue;
    int exam_id;
    int student_id;
    int score = 0;
    String course;
    HashMap result;
    boolean isExamStart;

    GetResultListener listener;

    public TakeExamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (GetResultListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_take_exam, container, false);
        tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);
        rbAns1 = (RadioButton) view.findViewById(R.id.rbAns1);
        rbAns2 = (RadioButton) view.findViewById(R.id.rbAns2);
        rbAns3 = (RadioButton) view.findViewById(R.id.rbAns3);
        rbAns4 = (RadioButton) view.findViewById(R.id.rbAns4);
        btnNext = (Button) view.findViewById(R.id.btn_next);
        rgAnswers = (RadioGroup) view.findViewById(R.id.rgAnswers);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        exam_id = getArguments().getInt("exam_id");
        course = getArguments().getString("course");
        student_id = getArguments().getInt(getString(R.string.student_id_argument));

        loadQuestions();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateResult();
                if (currentQue < (questions.size() - 1)) {
                    currentQue++;
                    displayQue();
                } else {
                    // Exam over so update the result and verify_status - DONE
                    updateResult();
                    showResult();
                    // unpin the screen
                    unpin();
                    isExamStart = false;

                }
                service.takePhoto(getActivity(),student_id,exam_id);
            }
        });
    }

    private void showResult() {
        listener.getResult(score, course);
    }

    private void updateResult() {
        SEMSRestClient client = new SEMSRestClient();

        String url = "/update/result";
        RequestParams params = new RequestParams();
        params.put(getString(R.string.exam_id_param), exam_id);
        JSONArray arr = new JSONArray();
        try {
            Iterator it = result.entrySet().iterator();
            while (it.hasNext()) {

                JSONObject obj = new JSONObject();
                Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                obj.put("que_id", entry.getKey());
                obj.put("result", entry.getValue());
                arr.put(obj);

            }
            params.put(getString(R.string.result_param), arr);
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(isAdded()) {
                        Log.i("RESULT ", getString(R.string.result_updated_successfully_text));
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(isAdded()) {
                        Log.i("RESULT ", getString(R.string.unable_to_update_result_text));
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void calculateResult() {
        Question que = questions.get(currentQue);
        String selectedAns = "";
        switch (rgAnswers.getCheckedRadioButtonId()) {
            case R.id.rbAns1:
                selectedAns = rbAns1.getText().toString();
                rbAns1.setChecked(false);
                break;
            case R.id.rbAns2:
                selectedAns = rbAns2.getText().toString();
                rbAns2.setChecked(false);
                break;
            case R.id.rbAns3:
                selectedAns = rbAns3.getText().toString();
                rbAns3.setChecked(false);
                break;
            case R.id.rbAns4:
                selectedAns = rbAns4.getText().toString();
                rbAns4.setChecked(false);
                break;
        }
        if (que.correctans.equals(selectedAns)) {
            result.put(que.id, CORRECT);
            score++;
        } else{
            result.put(que.id, WRONG);
        }


        rgAnswers.setSelected(false);

    }

    private void loadQuestions() {
        questions = new ArrayList<>();
        SEMSRestClient client = new SEMSRestClient();
        String url = "/questions/list";
        RequestParams params = new RequestParams();
        params.put("exam_id", exam_id);
        // pinup the screen and start the exam
        pin();
        isExamStart = true;

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(isAdded()) {
                    System.out.print(responseBody);
                    result = new HashMap();

                    try {
                        String response = new String(responseBody, "UTF-8");
                        JSONObject obj = new JSONObject(response);
                        JSONArray arr = obj.getJSONArray("list");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject que = arr.getJSONObject(i);
                            questions.add(new Question(que.getInt("question_id") + "", que.getString("question"), que.getString("ans1"), que.getString("ans2"),
                                    que.getString("ans3"), que.getString("ans4"), que.getString("correct_ans")));
                        }
                        currentQue = 0;
                        displayQue();

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
                    Toast.makeText(getActivity(), getString(R.string.unable_to_load_questions_text), Toast.LENGTH_SHORT).show();
                }

                Log.d("Question Loader", statusCode + "-----");

            }
        });
    }

    void pin() {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (!am.isInLockTaskMode()) {
            getActivity().startLockTask();
        }
    }
    void unpin() {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (!am.isInLockTaskMode()) {
            getActivity().stopLockTask();
        }
    }

    private void displayQue() {
        Question que = questions.get(currentQue);
        tvQuestion.setText(que.question);
        // Logic to jumble the answers
        RadioButton[] rbids = {rbAns1, rbAns2, rbAns3, rbAns4};

        int size = 4;

        ArrayList<RadioButton> list = new ArrayList<RadioButton>(size);
        for (int i = 0; i < size; i++) {
            list.add(rbids[i]);
        }

        Random rand = new Random();

        int index = rand.nextInt(list.size());
        (list.get(index)).setText(que.ans1);
        System.out.println("Selected: " + list.remove(index));

        index = rand.nextInt(list.size());
        (list.get(index)).setText(que.ans2);
        System.out.println("Selected: " + list.remove(index));


        index = rand.nextInt(list.size());
        (list.get(index)).setText(que.ans3);
        System.out.println("Selected: " + list.remove(index));

        index = rand.nextInt(list.size());
        (list.get(index)).setText(que.ans4);
        System.out.println("Selected: " + list.remove(index));

    }

    @Override
    public void onStop() {
        super.onStop();
        if (isExamStart) {
            logout();

        }
        if(flagBound) {
            getActivity().unbindService(connection);
            connection = null;
            flagBound = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void logout() {
        updateStatus();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

    private void updateStatus(){
        SEMSRestClient client = new SEMSRestClient();

        String url="/update/status";
        RequestParams params = new RequestParams();

        params.put(getString(R.string.exam_id_param),exam_id);
        params.put(getString(R.string.status_param),"NO");

        client.post(url,params,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public interface GetResultListener{
        public void getResult(int score, String course);
    }
    private CameraService service;
    boolean flagBound;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = ((CameraService.MyBinder) iBinder).getService();
            flagBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            flagBound = false;

        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Intent i = new Intent(getActivity(), CameraService.class);
        getActivity().bindService(i,connection, Context.BIND_AUTO_CREATE);
    }
}
