package com.server.sems;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.server.sems.database.DBConnection;
import com.server.sems.utility.Helper;
import com.server.sems.utility.LoginUser;
//Path: http://localhost/sems/login
@Path("/login")
public class Login {
	// HTTP Get Method
    @GET
    // Path: http://localhost/sems/login/dologin
    @Path("/dologin")
    // Produces JSON as response
    @Produces(MediaType.APPLICATION_JSON) 
    // Query parameters are parameters: http://localhost/sems/login/dologin?username=abc&password=xyz
    public String doLogin(@QueryParam("username") String uname, @QueryParam("password") String pwd){
        String response = "";
        System.out.println("Inside dologin **"+ uname+" **" +pwd);
        LoginUser user = checkCredentials(uname, pwd) ;
        if(user != null){
            response = Helper.constructJSON("login",true, user.id, user.type);
        }else{
            response = Helper.constructJSON("login", false, "Incorrect Email or Password");
        }
    return response;        
    }
 
    /**
     * Method to check whether the entered credential is valid
     * 
     * @param email
     * @param pwd
     * @return
     */
    private LoginUser checkCredentials(String email, String pwd){
        System.out.println("Inside checkCredentials");
        LoginUser user = null;
        int result = -1;
        if(Helper.isNotNull(email) && Helper.isNotNull(pwd)){
            try {
                user = DBConnection.checkLogin(email, pwd);
                System.out.println("Inside checkCredentials try "+result);
            } catch (Exception e) {
                System.out.println("Inside checkCredentials catch"+e.getMessage());
                
            }
        }else{
            System.out.println("Inside checkCredentials else");
            
        }
 
        return user;
    }
 
}
