package com.server.sems.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

import com.server.sems.utility.Exam;
import com.server.sems.utility.LoginUser;
import com.server.sems.utility.Photo;
import com.server.sems.utility.Question;
import com.server.sems.utility.Student;


public class DBConnection {
	@SuppressWarnings("finally")
	public static Connection createConnection() throws Exception {
		Connection con = null;
		try {
			Class.forName(Database.dbClass);
			con = DriverManager.getConnection(Database.url, Database.user, Database.password);
		} catch (Exception e) {
			throw e;
		} finally {
			return con;
		}
	}

	public static LoginUser checkLogin(String email, String pwd) throws Exception {

		LoginUser user = null; 
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();
			String query = "SELECT _id, type FROM user WHERE email = '" + email
					+ "' AND password=" + "'" + pwd + "'";
			System.out.println(query);
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				user = new LoginUser();
				System.out.println(rs.getInt(1));
				user.id = rs.getInt("_id");
				user.type = rs.getString("type");

			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if(rs != null){
				rs.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return user;
	}
	public static boolean insertUser(String name, String email, String pwd, String type, String photo) 
			throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		PreparedStatement stmt = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String query = "INSERT into user(name, email, password, type,photo) values( ?,?,?,?,?)";
			//			+ "'"+name+ "','" + email + "','" + pwd + "','" + type + "', '"+photo+"' )";
			System.out.println(query);
			stmt = dbConn.prepareStatement(query);
			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setString(3, pwd);
			stmt.setString(4, type);

			InputStream is = new FileInputStream(photo);
			stmt.setBinaryStream(5, is);
			int records = stmt.executeUpdate();

			System.out.println(records);
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	public static boolean insertSubject(String name) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		Statement stmt  = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();
			String query = "INSERT into subject(subject) values('"+name+ "')";
			int records = stmt.executeUpdate(query);
			System.out.println(records);
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	public static boolean insertQuestion(String question, String ans1,
			String ans2, String ans3, String ans4, String correct_ans,
			String level, String course) throws SQLException {
		boolean insertStatus = false;
		Connection dbConn = null;
		Statement stmt = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();
			String query = "INSERT into question(question, ans1, ans2, ans3, ans4, correct_ans, course, level) values('"
					+ question + "', '" + ans1 +"', '" + ans2 +"', '" + ans3 +"', '" + ans4 +"', '" + correct_ans +"', '" + course +"', '" + level + "')";

			int records = stmt.executeUpdate(query);
			System.out.println(records);
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	public static boolean insertPhotos(int exam_id, int student_id, String photo, Timestamp time) throws SQLException, Exception {
		boolean insertStatus = false;
		Connection dbConn = null;
		PreparedStatement stmt = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			String query = "INSERT into photos(exam_id, student_id, time, photo) values(?, ?, ?, ?)";
			stmt = dbConn.prepareStatement(query);
			stmt.setInt(1, exam_id);
			stmt.setInt(2, student_id);

			stmt.setTimestamp(3, time);
			InputStream is = new FileInputStream(photo);
			stmt.setBinaryStream(4, is);

			int records = stmt.executeUpdate();
			System.out.println(records);
			if (records > 0) {
				insertStatus = true;
			}
		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;
	}
	public static int addExam(int student_id, String course, String level) throws SQLException {

		int insertStatus = -1;
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet rsVerification = null, rsQuestions = null;
		PreparedStatement question_stmt = null, insert_stmt = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			int exam_id;
			stmt = dbConn.createStatement();
			String query = "INSERT into verification(student_id, course, level, verify_status) values("+ student_id+",'"+course+ "', '"+ level+"', 'NO')";
			if(stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)>0){

				rsVerification = stmt.getGeneratedKeys();
				if(rsVerification.next()){
					exam_id = rsVerification.getInt(1);
					String question_query = "select _id, correct_ans from question where course = ? and level = ? ORDER BY RAND() LIMIT 5";
					question_stmt = dbConn.prepareStatement(question_query);
					question_stmt.setString(1, course);
					question_stmt.setString(2, level);
					rsQuestions = question_stmt.executeQuery( );

					if(rsQuestions.first()){
						String insert_query = "INSERT into exam(exam_id, question_id, correct_ans) values(?, ?, ?)";
						insert_stmt = dbConn.prepareStatement(insert_query);
						insert_stmt.setInt(1, exam_id);
						do{


							insert_stmt.setInt(2, rsQuestions.getInt("_id"));
							insert_stmt.setString(3, rsQuestions.getString("correct_ans"));

							int records = insert_stmt.executeUpdate();
							System.out.println(records);
							if (records > 0) {
								insertStatus = 1;
							}		
						}while(rsQuestions.next());
					}
					else {
						insertStatus = 2;
					}
				}
			}else{
				insertStatus = 3;
			}


		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if(rsVerification != null){
				rsVerification.close();
			}
			if(insert_stmt != null){
				insert_stmt.close();
			}
			if(question_stmt != null){
				question_stmt.close();
			}
			if(rsQuestions != null){
				rsQuestions.close();
			}
			
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return insertStatus;

	}
	public static boolean updateResult(int exam_id, int que_id, String result) throws SQLException, Exception {
		boolean updateStatus = false;
		Connection dbConn = null;
		Statement stmt = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();
			updateStatus = stmt.execute("update exam set result = '"+result+"' where exam_id = "+exam_id+ " and question_id = "+que_id);

		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return updateStatus;
	}

	public static boolean updateStatus(int exam_id, String status) throws SQLException, Exception {
		boolean updateStatus = false;
		Connection dbConn = null;
		Statement stmt = null;
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();
			updateStatus = stmt.execute("update verification set verify_status = '"+status+"' where _id = "+exam_id );

		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			if (dbConn != null) {
				dbConn.close();
			}
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return updateStatus;
	}

	public static List<String> getSubjects() throws SQLException {
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet subjects = null;
		List<String> list = new ArrayList<String>();
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();

			subjects = stmt.executeQuery("select subject from subject");
			if(subjects.first()){

				do{
					list.add(subjects.getString("subject"));
				}while(subjects.next());
			}

		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if(subjects != null){
				subjects.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return list;
	}
	public static List<Exam> getExams(int student_id) throws SQLException {
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet exams = null;
		List<Exam> list = new ArrayList<Exam>();
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();

			exams = stmt.executeQuery("select _id, course, level from verification where verify_status= 'YES'");
			
			if(exams.first()){

				do{

					list.add(new Exam(exams.getInt("_id"), student_id, exams.getString("course"), exams.getString("level")));
				}while(exams.next());
			}

		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if(exams != null){
				exams.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return list;
	}

	public static List<Question> getQuestions(int exam_id) throws SQLException {
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet questions = null;
		List<Question> list = new ArrayList<Question>();
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();

			String query = "select _id, question, ans1, ans2, ans3, ans4, correct_ans from question where question._id in "+
					"(select question_id from exam where exam_id = "+exam_id+") order by rand()";
			questions = stmt.executeQuery(query);
			
			if(questions.first()){

				do{

					list.add(new Question(questions.getInt("_id"), questions.getString("question"),  questions.getString("ans1"), questions.getString("ans2"),
							questions.getString("ans3"), questions.getString("ans4"), questions.getString("correct_ans")));
				}while(questions.next());
			}

		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if(questions != null){
				questions.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return list;

	}

	public static List<Student> getStudents(String status) throws SQLException {
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet students = null;
		List<Student> list = new ArrayList<Student>();
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();

			students = stmt.executeQuery("select user._id as student_id, name, course, level, photo, verification._id as exam_id from user, verification "+
					" where verification.student_id = user._id and verify_status = '"+status+"'");
			if(students.first()){

				do{
					Blob photoData = students.getBlob("photo");
					Base64.Encoder encoder = Base64.getEncoder();
					
					String encodedPhotoData = org.apache.commons.codec.binary.StringUtils.newStringUtf8(org.apache.
							commons.codec.binary.Base64.encodeBase64(photoData.getBytes(1, (int)photoData.length())));
					list.add(new Student(students.getInt("student_id"), students.getInt("exam_id"), students.getString("name"), students.getString("course"),encodedPhotoData,
							students.getString("level")));
				}while(students.next());
			}

		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if(students != null){
				students.close();
			}
			
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return list;
	}

	public static List<Photo> getPhotos(int student_id, int exam_id) throws SQLException {
		Connection dbConn = null;
		Statement stmt = null;
		ResultSet photos = null;
		List<Photo> list = new ArrayList<Photo>();
		try {
			try {
				dbConn = DBConnection.createConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = dbConn.createStatement();

			String query = "select _id, photo, time from photos where student_id = " + student_id + " and exam_id = "+exam_id;
			photos = stmt.executeQuery(query);
			
			if(photos.first()){

				do{
					Blob photoData = photos.getBlob("photo");
					Base64.Encoder encoder = Base64.getEncoder();
					
					String encodedPhotoData = org.apache.commons.codec.binary.StringUtils.newStringUtf8(org.apache.
							commons.codec.binary.Base64.encodeBase64(photoData.getBytes(1, (int)photoData.length())));

					list.add(new Photo(photos.getInt("_id"), encodedPhotoData,  photos.getString("time")));
				}while(photos.next());
			}

		} catch (SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			throw e;
		} finally {
			if(stmt != null){
				stmt.close();
			}
			if(photos != null){
				photos.close();
			}
			if (dbConn != null) {
				dbConn.close();
			}
		}
		return list;
	}

}
