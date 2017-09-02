package com.server.sems.utility;

// POJO class
public class Student {
	int _id;
	int exam_id;
	String name;
//	byte[] photo;
	String encodedPhoto;
	String course;
	String level;
	public Student(int _id, int exam_id, String name, String course, String encodedPhoto,
			String level) {
		super();
		this._id = _id;
		this.exam_id = exam_id;
		this.name = name;
//		this.photo = photo;
		this.encodedPhoto = encodedPhoto;
		this.course = course;
		this.level = level;
	}
	
}