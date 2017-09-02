package com.server.sems;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.server.sems.database.DBConnection;
import com.server.sems.utility.Helper;

//Path: http://localhost/sems/update
@Path("/update")
public class Update {
	//HTTP Post Method
	@POST
	// Path: http://localhost/sems/update/result
	@Path("/result")  
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	// Query parameters are parameters: http://localhost/sems/update/result?result=xyz
	public String updateResult(@FormParam("exam_id") int exam_id, @FormParam("result") JSONArray result){
		String response = "";
		System.out.println("Inside updateResult "+result);
		int retCode = updateExamResult(exam_id, result);
		if(retCode == 0){
			response = Helper.constructJSON("updateResult",true);
		}else if(retCode == 1){
			response = Helper.constructJSON("updateResult",false, "Sql exception");
		}else if(retCode == 2){
			response = Helper.constructJSON("updateResult",false, "Error occured");
		}
		return response;

	}

	// HTTP Post Method
	private int updateExamResult(int exam_id, JSONArray results) {
		System.out.println("Inside updateExamResult");
		int result = 23;

		for(int i=0; i<results.length();i++){
			JSONObject obj;
			try {
				obj = results.getJSONObject(i);

				System.out.println("question "+ obj.getString("que_id"));
				System.out.println("result "+obj.getString("result"));
				int que_id = Integer.parseInt(obj.getString("que_id"));
				String que_result = obj.getString("result");
				if(DBConnection.updateResult(exam_id, que_id, que_result)){
					System.out.println("updateExamResult if");
					result = 0;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println("updateExamResult catch sqle");
				result = 1;
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("updateExamResult catch sqle");
				result = 1;
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside updateExamResult catch e "+e.getMessage());
				result = 2;
				e.printStackTrace();
			} 

		}
		updateExamStatus(exam_id, "DONE");
		return result;
	}


	@POST
	// Path: http://localhost/sems/update/status
	@Path("/status")  
	// Produces JSON as response
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes("application/x-www-form-urlencoded")
	// Query parameters are parameters: http://localhost/sems/update/status?name=pqrs&username=abc&password=xyz
	public String updateStatus(@FormParam("exam_id") int exam_id, @FormParam("status") String status){
		String response = "";
		System.out.println("Inside updateStatus "+exam_id+" "+status);
		int retCode = updateExamStatus(exam_id, status);
		if(retCode == 0){
			response = Helper.constructJSON("updateStatus",true);
		}else if(retCode == 1){
			response = Helper.constructJSON("updateStatus",false, "You are already registered");
		}else if(retCode == 2){
			response = Helper.constructJSON("updateStatus",false, "Error occured");
		}
		return response;

	}

	private int updateExamStatus(int exam_id, String status){
		System.out.println("Inside updateExamStatus");
		int result = 2;
		if(Helper.isNotNull(status)){


			try {
				
				if(DBConnection.updateStatus(exam_id, status)){
					System.out.println("updateExamStatus if");
					result = 0;
				}
			} catch(SQLException sqle){
				System.out.println("updateExamStatus catch sqle");
				//When Primary key violation occurs that means user is already registered
				if(sqle.getErrorCode() == 1062){
					result = 1;
				} 
				
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Inside updateExamStatus catch e ");
				result = 2;
			}
		}else{
			System.out.println("Inside updateExamStatus else");
			result = 2;
		}

		return result;
	}
}