package com.droovy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseOp {


	//A SUPPRIMER

	private static String tokenGoogleDrive;
	private static String tokenDropBox;
	private static String tokenOneDrive;

	private Connection conn;
	
	private String url = "./" + "users.sqlite";

	private String sql_create_table = "CREATE TABLE IF NOT EXISTS users (\n"
			+ "	id integer PRIMARY KEY AUTOINCREMENT,\n"
			+ "	name TEXT NOT NULL,\n"
			+ "	password TEXT NOT NULL,\n"
			+ " googledrivetoken TEXT,\n"
			+ " dropboxtoken TEXT,\n"
			+ " onedrivetoken TEXT\n"
			+ ");";





	private Connection connectDb() throws SQLException, ClassNotFoundException{
		
		Class.forName("org.sqlite.JDBC");
		
		conn = DriverManager.getConnection("jdbc:sqlite:"+url);
		conn.createStatement().executeUpdate(sql_create_table);
		return conn;
	}


	public int createUser(String name, String password){

		String sql_create_user = "INSERT INTO users (name,password) values (?,?);";

		String sql_get_user_id = "SELECT MAX(id) as id FROM users";
		
		try {
			Connection conn;
			try {
				conn = connectDb();
			} catch (ClassNotFoundException e) {
				return -1;
			}
			PreparedStatement st = conn.prepareStatement(sql_create_user);
			st.setString(1, name);
			st.setString(2, password);
			st.executeUpdate();
			

			ResultSet rs = conn.createStatement().executeQuery(sql_get_user_id);

			while (rs.next()) {
				return rs.getInt("id");
			}
			conn.close();
		
			return -1;

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}

	}

	public boolean checkIfUserExist(String name,String password) {
		String sql_get_user = "SELECT id FROM users WHERE name == ? and password == ?";
		
		try {
			Connection conn;
			try {
				conn = connectDb();
			} catch (ClassNotFoundException e) {
				return false;
			}
			PreparedStatement st = conn.prepareStatement(sql_get_user);
			st.setString(1, name);
			st.setString(2, password);
			st.execute();
			
			
			ResultSet rs = st.getResultSet();
			
			while (rs.next()) {
				int id =  rs.getInt("id");
				conn.close();
				return true;
			}
			return false;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	

	public int authUser(String name, String password){
		
		String sql_get_user = "SELECT id FROM users WHERE name == ? and password == ?";
		
		try {
			Connection conn;
			try {
				conn = connectDb();
			} catch (ClassNotFoundException e) {
				return -1;
			}
			PreparedStatement st = conn.prepareStatement(sql_get_user);
			st.setString(1, name);
			st.setString(2, password);
			st.execute();
			
			
			ResultSet rs = st.getResultSet();
			
			while (rs.next()) {
				int id =  rs.getInt("id");
				conn.close();
				return id;
			}
			return -1;

		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
		
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


	public void close() throws SQLException {
		conn.close();
	}


}
