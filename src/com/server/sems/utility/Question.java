package com.server.sems.utility;

// POJO class
public class Question {
	int question_id;
	String question;
	String ans1;
	String ans2;
	String ans3;
	String ans4;
	String correct_ans;
	public Question(int question_id, String question, String ans1, String ans2, String ans3,
			String ans4, String correct_ans) {
		super();
		this.question_id = question_id;
		this.question = question;
		this.ans1 = ans1;
		this.ans2 = ans2;
		this.ans3 = ans3;
		this.ans4 = ans4;
		this.correct_ans = correct_ans;
	}
	
	
}
