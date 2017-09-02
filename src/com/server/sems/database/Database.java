package com.server.sems.database;

public class Database {
	public static String dbClass = "com.mysql.jdbc.Driver";
	private static String name= "sems";
    public static String url = "jdbc:mysql://localhost:3306/"+name;
    public static String user = "root";
    public static String password = "root";
}
