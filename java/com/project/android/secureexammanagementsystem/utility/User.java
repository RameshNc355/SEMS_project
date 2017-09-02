package com.project.android.secureexammanagementsystem.utility;

/**
 * Created by vishakha on 03-03-2017.
 */
public class User {
    public String id;
    public String name;
    public String email;
    public String password;
    public String photo;
    public String type;
    public User(String id, String name, String email, String password, String photo, String type){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.type = type;
    }

}
