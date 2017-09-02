package com.project.android.secureexammanagementsystem.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.android.secureexammanagementsystem.R;

import java.util.List;

/**
 * Created by vishakha on 14-03-2017.
 */
public class StudentArrayAdapter extends ArrayAdapter {
    Context context;
    List<Student> students;
    int resourceID;
    public StudentArrayAdapter(Context context, int resource, List<Student> students) {
        super(context, resource);
        this.context = context;
        this.students = students;
        resourceID = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tvName;
        TextView tvCourse;
        TextView tvLevel;
        if(convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resourceID, parent, false);
        }
        tvName = (TextView) convertView.findViewById(R.id.tvName);
        tvCourse = (TextView) convertView.findViewById(R.id.tvCourse);
        tvLevel = (TextView) convertView.findViewById(R.id.tvLevel);

        Student student = students.get(position);
        tvName.setText(student.name);
        tvCourse.setText(student.course);
        tvLevel.setText(student.level);
        return convertView;
    }

    @Override
    public Student getItem(int position) {
        Log.d("getItem", position+"");
        if(position>0)
        return students.get(position-1);
        else
            return null;
    }

    @Override
    public int getCount() {
        return students.size();
    }

}
