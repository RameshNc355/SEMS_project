package com.server.sems.utility;

// POJO class
public class Exam {
	int exam_id;
	int student_id;
	String course;
	String level;
	
	public Exam(int exam_id, int student_id, String course, String level) {
		this.exam_id = exam_id;
		this.student_id = student_id;
		this.course = course;
		this.level = level;
	}
	
}
