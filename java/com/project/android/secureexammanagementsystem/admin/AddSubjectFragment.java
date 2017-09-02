package com.project.android.secureexammanagementsystem.admin;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.android.secureexammanagementsystem.R;
import com.project.android.secureexammanagementsystem.utility.Helper;
import com.project.android.secureexammanagementsystem.utility.SEMSRestClient;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddSubjectFragment extends Fragment {

    EditText etSubject;
    Button btnAddSubject;
    public AddSubjectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_subject, container, false);
        etSubject = (EditText)view.findViewById(R.id.etSubject);
        btnAddSubject = (Button)view.findViewById(R.id.btn_add_subject);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = etSubject.getText().toString();
                if(!Helper.isNotNull(subject)){
                    Toast.makeText(getContext(), getString(R.string.empty_subject_text), Toast.LENGTH_SHORT).show();
                    return;
                }
                SEMSRestClient client = new SEMSRestClient();

                String url="/insert/subject";
                RequestParams params = new RequestParams();
                params.put(getString(R.string.name_param),subject);

                client.post(url,params,new AsyncHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        System.out.print(responseBody);
                        String response = responseBody.toString();
                        Toast.makeText(getContext(),getString(R.string.insert_subject_success_text),Toast.LENGTH_LONG).show();
                        etSubject.setText("");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getContext(),getString(R.string.insert_subject_failed_text),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
