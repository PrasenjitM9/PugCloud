package com.droovy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseOp {


	//A SUPPRIMER

	private static String tokenGoogleDrive;
	private static String tokenDropBox;
	private static String tokenOneDrive;

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


	public static void updateUserOneDriveToken(String token){
		tokenOneDrive = token;
	}

	public static void updateUserDropBoxToken(String token){
		tokenDropBox = token;
	}

	public static void updateUserGoogleDriveToken(String token){
		tokenGoogleDrive = token;
	}





	public static String getUserOneDriveToken(){
		return tokenOneDrive;
	}

	public static String getUserDropBoxToken(){
		return tokenDropBox;
	}

	public static String getUserGoogleDriveToken(){
		return tokenGoogleDrive;
	}




}
