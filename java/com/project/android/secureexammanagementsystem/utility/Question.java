package com.project.android.secureexammanagementsystem.utility;

/**
 * Created by vishakha on 03-03-2017.
 */
public class Question {
    public String id;
    public String question;
    public String ans1;
    public String ans2;
    public String ans3;
    public String ans4;
    public String correctans;
    /*public String level;
    public String course;*/
    public Question(String id, String question, String ans1, String ans2, String ans3, String ans4, String correctans){//}, String level, String course){
        this.id = id;
        this.question = question;
        this.ans1 = ans1;
        this.ans2 = ans2;
        this.ans3 = ans3;
        this.ans4 = ans4;
        this.correctans = correctans;
        /*this.level = level;
        this.course = course;*/
    }
}
