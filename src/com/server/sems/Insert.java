package com.server.sems;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.server.sems.database.DBConnection;
import com.server.sems.utility.Helper;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.prism.Image;

//Path: http://localhost/sems/insert
@Path("/insert")
public class Insert {
	//HTTP Post Method
	@POST
	// Path: http://localhost/sems/insert/subject
	@Path("/subject")  
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://localhost/sems/insert/subject?name=xyz
	public String addsubject(@FormParam("name") String name){
		String response = "";
		System.out.println("Inside addSubject "+name);
		int retCode = insertSubject(name);
		if(retCode == 0){
			response = Helper.constructJSON("addSubject",true);
		}else if(retCode == 1){
			response = Helper.constructJSON("addSubject",false, "Subject is already available");
		}else if(retCode == 2){
			response = Helper.constructJSON("addSubject",false, "Special Characters are not allowed in Subjctname");
		}else if(retCode == 3){
			response = Helper.constructJSON("addSubject",false, "Error occured");
		}
		return response;

	}

	// HTTP Post Method
	private int insertSubject(String name) {
		System.out.println("Inside insertSubject");
		int result = 3;
		if(Helper.isNotNull(name)){
			try {
				if(DBConnection.insertSubject(name)){
					System.out.println("insertSubject if");
					result = 0;
				}
			} catch(SQLException sqle){
				System.out.println("insertSubject catch sqle");
				//When Primary key violation occurs that means user is already registered
				if(sqle.getErrorCode() == 1062){
					result = 1;
				} 
				//When special characters are used in name,username or password
				else if(sqle.getErrorCode() == 1064){
					System.out.println(sqle.getErrorCode());
					result = 2;
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside insertSubject catch e "+e.getMessage());
				result = 3;
			}
		}else{
			System.out.println("Inside insertSubject else");
			result = 3;
		}


		return result;
	}


	@POST
	// Path: http://localhost/sems/insert/user
	@Path("/user")  
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	// Query parameters are parameters: http://localhost/sems/register/doregister?name=pqrs&username=abc&password=xyz
	public String addUser(@FormParam("name") String name, @FormParam("email") String email, @FormParam("password") String pwd, @FormParam("type") String type, @FormParam("photo") String photo){

		String response = "";
		
		System.out.println("Inside insertUser "+email+"  "+pwd);
		int retCode = insertUser(name, email, pwd, type, photo);
		if(retCode == 0){
			response = Helper.constructJSON("insertUser",true);
		}else if(retCode == 1){
			response = Helper.constructJSON("insertUser",false, "You are already registered");
		}else if(retCode == 2){
			response = Helper.constructJSON("insertUser",false, "Special Characters are not allowed in Username and Password");
		}else if(retCode == 3){
			response = Helper.constructJSON("insertUser",false, "Error occured");
		}
		return response;

	}

	private int insertUser(String name, String email, String pwd, String type, String photoPath){
		System.out.println("Inside insertUser");
		int result = 3;
		
		if(Helper.isNotNull(name) && Helper.isNotNull(email) && Helper.isNotNull(pwd) && Helper.isNotNull(type) && photoPath!=null)
		{

			try {
				

				if(DBConnection.insertUser(name, email, pwd, type, photoPath)){
					System.out.println("insertUser if");
					result = 0;
				}
			} catch(SQLException sqle){
				System.out.println("insertUser catch sqle"+sqle.getMessage());
				//When Primary key violation occurs that means user is already registered
				if(sqle.getErrorCode() == 1062){
					result = 1;
				} 
				//When special characters are used in name,username or password
				else if(sqle.getErrorCode() == 1064){
					System.out.println(sqle.getErrorCode());
					result = 2;
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside insertUser catch e ");
				result = 3;
			}
		}
		else{
			System.out.println("Inside insertUser else");
			result = 3;
		}

		return result;
	}

	@POST
	// Path: http://localhost/sems/insert/question
	@Path("/question")  
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes("application/x-www-form-urlencoded")
	// Query parameters are parameters: http://localhost/sems/register/doregister?name=pqrs&username=abc&password=xyz
	public String addQuestion(@FormParam("question") String question, @FormParam("ans1") String ans1, @FormParam("ans2") String ans2, 
			@FormParam("ans3") String ans3, @FormParam("ans4") String ans4, @FormParam("correct_ans") String correct_ans, 
			@FormParam("subject") String course, @FormParam("level") String level){
		String response = "";
		System.out.println("Inside insertQuestion "+question+"  "+correct_ans);
		int retCode = insertQuestion(question, ans1, ans2, ans3, ans4, correct_ans, course, level);
		if(retCode == 0){
			response = Helper.constructJSON("insertQuestion",true);
		}else if(retCode == 1){
			response = Helper.constructJSON("insertQuestion",false, "Question is already registered");
		}else if(retCode == 2){
			response = Helper.constructJSON("insertQuestion",false, "Special Characters are not allowed in Username and Password");
		}else if(retCode == 3){
			response = Helper.constructJSON("insertQuestion",false, "Error occured");
		}
		return response;

	}

	private int insertQuestion(String question, String ans1, String ans2, String ans3, String ans4, String correct_ans, String course, String level){
		System.out.println("Inside insertQuestion");
		int result = 3;
		if(Helper.isNotNull(question) && Helper.isNotNull(ans1) && Helper.isNotNull(ans2) && Helper.isNotNull(ans3) && Helper.isNotNull(ans4)
				&& Helper.isNotNull(correct_ans) && Helper.isNotNull(course) && Helper.isNotNull(level)){


			try {
				System.out.println(course);
				if(DBConnection.insertQuestion(question, ans1, ans2, ans3, ans4, correct_ans, level, course)){
					System.out.println("insertQuestion if");
					result = 0;
				}
			} catch(SQLException sqle){
				System.out.println("insertQuestion catch sqle"+sqle.getMessage());
				//When Primary key violation occurs that means user is already registered
				if(sqle.getErrorCode() == 1062){
					result = 1;
				} 
				//When special characters are used in name,username or password
				else if(sqle.getErrorCode() == 1064){
					System.out.println(sqle.getErrorCode());
					result = 2;
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside insertQuestion catch e ");
				result = 3;
			}
		}else{
			System.out.println("Inside insertQuestion else");
			result = 3;
		}

		return result;
	}

	
	@POST
	// Path: http://localhost/sems/insert/photos
	@Path("/photos")  
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes("application/x-www-form-urlencoded")
	// Query parameters are parameters: http://localhost/sems/register/doregister?name=pqrs&username=abc&password=xyz
	public String addPhotos(@FormParam("exam_id") int exam_id, @FormParam("student_id") int student_id, @FormParam("photo") String photo){
		String response = "";
		System.out.println("Inside insertPhotos "+exam_id+"  "+student_id);
				int retCode = insertPhotos(exam_id, student_id, photo);
		if(retCode == 0){
			response = Helper.constructJSON("insertPhotos",true);
		}else if(retCode == 1){
			response = Helper.constructJSON("insertPhotos",false, "Photo is already registered");
		}else if(retCode == 2){
			response = Helper.constructJSON("insertPhotos",false, "Special Characters are not allowed in Username and Password");
		}else if(retCode == 3){
			response = Helper.constructJSON("insertPhotos",false, "Error occured");
		}
		return response;

	}

	private int insertPhotos(int exam_id, int student_id, String photo){
		System.out.println("Inside insertPhotos");
		int result = 3;
		if(photo!=null){


			try {
				/*String sRootPath = new File("").getAbsolutePath();
				
				File file = new File(sRootPath+"/"+exam_id+"_"+student_id+"_"+time+".jpg");
				java.nio.file.Path source = Paths.get(photo.getAbsolutePath());
				java.nio.file.Path destination = Paths.get(file.getAbsolutePath());

				Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);*/
				Timestamp time = new Timestamp(Calendar.getInstance().getTimeInMillis());
				
				if(DBConnection.insertPhotos(exam_id, student_id, photo, time)){
					System.out.println("insertPhotos if");
					result = 0;
				}
			} catch(SQLException sqle){
				System.out.println("insertPhotos catch sqle");
				//When Primary key violation occurs that means user is already registered
				if(sqle.getErrorCode() == 1062){
					result = 1;
				} 
				//When special characters are used in name,username or password
				else if(sqle.getErrorCode() == 1064){
					System.out.println(sqle.getErrorCode());
					result = 2;
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside insertPhotos catch e ");
				result = 3;
			}
		}else{
			System.out.println("Inside insertPhotos else");
			result = 3;
		}

		return result;
	}
	
	@POST
	// Path: http://localhost/sems/insert/exam
	@Path("/exam")  
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes("application/x-www-form-urlencoded")
	// Query parameters are parameters: http://localhost/sems/insert/exam?student_id=2&subject=android&level=1
	public String bookExam(@FormParam("student_id") int student_id, @FormParam("subject") String subject, @FormParam("level") String level){
		String response = "";
		System.out.println("Inside addExam "+student_id+"  "+level);
				int retCode = addExam(student_id, subject, level);
		if(retCode == 1){
			response = Helper.constructJSON("addExam",true);
		}else if(retCode == 2){
			response = Helper.constructJSON("addExam",false, "Not sufficient questions");
		}else if(retCode == 3){
			response = Helper.constructJSON("addExam",false, "Exam already created");
		}else if(retCode == -1){
			response = Helper.constructJSON("addExam",false, "Error occured");
		}
		return response;

	}

	private int addExam(int student_id, String course, String level){
		System.out.println("Inside addExam");
		int result = 2;
		
			try {
				
				if((result = DBConnection.addExam(student_id, course, level)) > 0){
					System.out.println("addExam if");

				}
			} catch(SQLException sqle){
				System.out.println("addExam catch sqle");
				//When Primary key violation occurs that means record is already entered
				if(sqle.getErrorCode() == 1062){
					result = 3;
				} 
				result = -1;
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside addExam catch e ");
				result = 2;
			}
		

		return result;
	}
	
	

}
