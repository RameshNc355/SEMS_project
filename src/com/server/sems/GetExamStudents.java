package com.server.sems;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.server.sems.database.DBConnection;
import com.server.sems.utility.Exam;
import com.server.sems.utility.Helper;
import com.server.sems.utility.Question;
import com.server.sems.utility.Student;

//Path: http://localhost/sems/students
@Path("/students")

public class GetExamStudents {
	ArrayList<Student> list;
	// HTTP Get Method
    @GET
    // Path: http://localhost/sems/students/list
    @Path("/list")
    // Produces JSON as response
    @Produces(MediaType.APPLICATION_JSON) 
    public String list(@QueryParam("status") String status){
        String response = "";
        System.out.println("Inside list students ");
        if(getStudents(status)){
            response = Helper.constructJSON("students", true, list);
        }else{
            response = Helper.constructJSON("students", false, list);
        }
    return response;        
    }
 
    /**
     * Method to getExams list
     * 
     * @return list of exams
     */
    private boolean getStudents(String status){
        System.out.println("Inside getStudents");
                
            try {
                list = (ArrayList<Student>) DBConnection.getStudents(status);
                System.out.println("Inside getStudents try "+list);
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Inside getStudents catch"+e.getMessage());
                return false;
            }
        return true;
    }
 

}
