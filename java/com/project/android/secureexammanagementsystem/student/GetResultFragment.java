package com.project.android.secureexammanagementsystem.student;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.project.android.secureexammanagementsystem.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetResultFragment extends Fragment {

    TextView tvSubject, tvScore;
    Button btnok;
    public GetResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_result, container, false);
        tvScore = (TextView) view.findViewById(R.id.tvScore);
        tvSubject = (TextView)view.findViewById(R.id.tvResultSubject);
        btnok = (Button)view.findViewById(R.id.btn_result_ok);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String score = (getArguments().getInt("score")) + " / 5";
        String course = getArguments().getString("course");
        tvSubject.setText(course);
        tvScore.setText(score);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

}
