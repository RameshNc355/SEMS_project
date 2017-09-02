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

//Path: http://localhost/sems/questions
@Path("/questions")

public class GetQuestions {
	ArrayList<Question> list;
	// HTTP Get Method
    @GET
    // Path: http://localhost/sems/questions/list
    @Path("/list")
    // Produces JSON as response
    @Produces(MediaType.APPLICATION_JSON) 
    public String list(@QueryParam("exam_id") String exam_id){
        String response = "";
        System.out.println("Inside list exams ");
        if(getQuestions(Integer.parseInt(exam_id))){
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
    private boolean getQuestions(int exam_id){
        System.out.println("Inside getQuestions");
                
            try {
                list = (ArrayList<Question>) DBConnection.getQuestions(exam_id);
                System.out.println("Inside getQuestions try "+list);
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Inside getQuestions catch"+e.getMessage());
                return false;
            }
        return true;
    }
 

}
