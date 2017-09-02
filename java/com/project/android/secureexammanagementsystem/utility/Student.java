package com.project.android.secureexammanagementsystem.utility;

/**
 * Created by vishakha on 14-03-2017.
 */
public class Student {
    public int _id;
    public int exam_id;
    public String name;
    public String photo;
    public String course;
    public String level;
    public Student(int _id, int exam_id, String name, String photo, String course,
                   String level) {
        super();
        this._id = _id;
        this.exam_id = exam_id;
        this.name = name;
        this.photo = photo;
        this.course = course;
        this.level = level;
    }
}
