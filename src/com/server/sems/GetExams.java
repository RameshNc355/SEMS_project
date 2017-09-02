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

//Path: http://localhost/sems/exams
@Path("/exams")

public class GetExams {
	ArrayList<Exam> list;
	// HTTP Get Method
    @GET
    // Path: http://localhost/sems/exams/list
    @Path("/list")
    // Produces JSON as response
    @Produces(MediaType.APPLICATION_JSON) 
    public String list(@QueryParam("student_id") int student_id){
        String response = "";
        System.out.println("Inside list exams ");
        if(getExams(student_id)){
            response = Helper.constructJSON("exams", true, list);
        }else{
            response = Helper.constructJSON("exams", false, list);
        }
    return response;        
    }
 
    /**
     * Method to getExams list
     * 
     * @return list of exams
     */
    private boolean getExams(int student_id){
        System.out.println("Inside getExams");
                
            try {
                list = (ArrayList<Exam>) DBConnection.getExams(student_id);
                System.out.println("Inside getExams try "+list);
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Inside getExams catch"+e.getMessage());
                return false;
            }
        return true;
    }
 

}
