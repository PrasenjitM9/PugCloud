package com.droovy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseOp {


	private String url = "./" + "users.sqlite";
	
	private String sql_create_table = "CREATE TABLE IF NOT EXISTS users (\n"
            + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
            + "	name text NOT NULL,\n"
            + "	password text NOT NULL,\n"
            + " googledrivetoken text,\n"
            + " dropboxtoken text,\n"
            + " onedrivetoken text,\n"
            + ");";





	private Connection connectDb() throws SQLException{
		Connection conn = DriverManager.getConnection(url);
		conn.createStatement().executeQuery(sql_create_table);
		return conn;
	}


	public int createUser(){
		
		String sql_create_user = "INSERT INTO users (\n"
	            + "	id integer PRIMARY KEY AUTOINCREMENT,\n"
	            + "	name text NOT NULL,\n"
	            + "	password text NOT NULL,\n"
	            + " googledrivetoken text,\n"
	            + " dropboxtoken text,\n"
	            + " onedrivetoken text,\n"
	            + ");";
		
		
		try {
			Connection conn = connectDb();
			conn.createStatement().executeQuery(sql_create_table);
			return 0;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
		
		
		

	}


	public void authUser(){

	}


	public void updateUserOneDriveToken(){

	}

	public void updateUserDropBoxToken(){

	}

	public void updateUserGoogleDriveToken(){

	}





	public void getUserOneDriveToken(){

	}

	public void getUserDropBoxToken(){

	}

	public void getUserGoogleDriveToken(){

	}




}
