package com.server.sems;

import java.io.File;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.server.sems.database.DBConnection;
import com.server.sems.utility.Photo;
import com.server.sems.utility.Helper;

//Path: http://localhost/sems/photos
@Path("/photos")

public class GetPhotos {
	ArrayList<Photo> list;
	// HTTP Get Method
    @GET
    // Path: http://localhost/sems/photos/list
    @Path("/list")
    // Produces JSON as response
    @Produces(MediaType.APPLICATION_JSON) 
    public String list(@QueryParam("student_id") int student_id, @QueryParam("exam_id") int exam_id){
        String response = "";
        System.out.println("Inside list photos ");
        if(getPhotos(student_id,exam_id)){
            response = Helper.constructJSON("photos", true, list);
        }else{
            response = Helper.constructJSON("photos", false, list);
        }
    return response;        
    }
 
    /**
     * Method to getPhotos list
     * 
     * @return list of photos
     */
    private boolean getPhotos(int student_id, int exam_id){
        System.out.println("Inside getPhotos");
                
            try {
                list = (ArrayList<Photo>) DBConnection.getPhotos(student_id, exam_id);
                System.out.println("Inside getPhotos try "+list);
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                System.out.println("Inside getPhotos catch"+e.getMessage());
                return false;
            }
        return true;
    }
 // HTTP Get Method
    @GET
    // Path: http://localhost/sems/photos/list
    @Path("/profile")
    // Produces JSON as response
    @Produces(MediaType.APPLICATION_JSON) 
    public Response list(@QueryParam("student_id") int student_id){
//        String response = "";
        System.out.println("Inside list photos ");
        String filepath = "C:\\Users\\vishakha\\Documents\\Andrroid based Maximum discount finder.pdf";
		System.out.println("Downloading file "+filepath);
		File file = new File(filepath);
		ResponseBuilder response = Response.ok((Object)file);
		response.header("Content-Disposition","attachment; filename=\"collections.pdf\"");
		return response.build();
        /*if(getPhotos(student_id)){
            response = Helper.constructJSON("photos", true, list);
        }else{
            response = Helper.constructJSON("photos", false, list);
        }
        return response;*/
           
    }
 
}
