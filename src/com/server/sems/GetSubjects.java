package com.server.sems;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.server.sems.database.DBConnection;
import com.server.sems.utility.Helper;
//Path: http://localhost/sems/subjects
@Path("/subjects")
public class GetSubjects {
	ArrayList<String> list;
	// HTTP Get Method
    @GET
    // Path: http://localhost/sems/subjects/list
    @Path("/list")
    // Produces JSON as response
    @Produces(MediaType.APPLICATION_JSON) 
    public String list(){
        String response = "";
        System.out.println("Inside list subject ");
        if(getSubjects()){
            response = Helper.constructJSON("subject", true, list);
        }else{
            response = Helper.constructJSON("subject", false, list);
        }
    return response;        
    }
 
    /**
     * Method to getSubjects list
     * 
     * @return list of subjects
     */
    private boolean getSubjects(){
        System.out.println("Inside getSubjects");
                
            try {
                list = (ArrayList<String>) DBConnection.getSubjects();
                System.out.println("Inside getSubjects try "+list);
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Inside getSubjects catch"+e.getMessage());
                return false;
            }
        return true;
    }
 
}
