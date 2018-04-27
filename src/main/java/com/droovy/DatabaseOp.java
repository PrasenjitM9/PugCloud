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

	public boolean checkIfUserExist(String name) {
		String sql_get_user = "SELECT id FROM users WHERE name == ?";
		
		try {
			try {
				conn = connectDb();
			} catch (ClassNotFoundException e) {
				return false;
			}
			PreparedStatement st = conn.prepareStatement(sql_get_user);
			st.setString(1, name);
			st.execute();
			
			
			ResultSet rs = st.getResultSet();
			
			while (rs.next()) {
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


	public boolean updateUserOneDriveToken(String token,String id){
		String sql_update_user = "UPDATE users SET onedrivetoken = ? WHERE id == ?;";

		
		try {
			try {
				conn = connectDb();
			} catch (ClassNotFoundException e) {
				return false;
			}
			PreparedStatement st = conn.prepareStatement(sql_update_user);
			st.setString(1, token);
			st.setString(2, id);
			st.executeUpdate();
		
			conn.close();
		
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateUserDropBoxToken(String token,String id){
		String sql_update_user = "UPDATE users SET dropboxtoken = ? WHERE id == ?;";

		
		try {
			try {
				conn = connectDb();
			} catch (ClassNotFoundException e) {
				return false;
			}
			PreparedStatement st = conn.prepareStatement(sql_update_user);
			st.setString(1, token);
			st.setString(2, id);
			st.executeUpdate();
		
			conn.close();
		
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}	
	}

	public boolean updateUserGoogleDriveToken(String token,String id){
		String sql_update_user = "UPDATE users SET googledrivetoken = ? WHERE id == ?;";

		
		try {
			try {
				conn = connectDb();
			} catch (ClassNotFoundException e) {
				return false;
			}
			PreparedStatement st = conn.prepareStatement(sql_update_user);
			st.setString(1, token);
			st.setString(2, id);
			st.executeUpdate();
		
			conn.close();
		
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}





	public String getUserOneDriveToken(String id){
		String sql_create_user = "SELECT onedrivetoken FROM users WHERE id == ?;";

		
		try {
			try {
				conn = connectDb();
			} catch (ClassNotFoundException e) {
				return "";
			}
			PreparedStatement st = conn.prepareStatement(sql_create_user);
			st.setString(1, id);
			ResultSet rs = st.executeQuery();

			
			while (rs.next()) {
				String token =  rs.getString("onedrivetoken")+"";
				conn.close();
				return token;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}

	public String getUserDropBoxToken(String id){
			String sql_create_user = "SELECT dropboxtoken FROM users WHERE id == ?;";

			
			try {
				try {
					conn = connectDb();
				} catch (ClassNotFoundException e) {
					return "";
				}
				PreparedStatement st = conn.prepareStatement(sql_create_user);
				st.setString(1, id);
				ResultSet rs = st.executeQuery();

				
				while (rs.next()) {
					String token =  rs.getString("dropboxtoken")+"";
					conn.close();
					return token;
				}

			} catch (SQLException e) {
				e.printStackTrace();
				return "";
			}
			return "";
	}

	public String getUserGoogleDriveToken(String id){
		String sql_create_user = "SELECT googledrivetoken FROM users WHERE id == ?;";

		try {
			try {
				conn = connectDb();
			} catch (ClassNotFoundException e) {
				return "";
			}
			PreparedStatement st = conn.prepareStatement(sql_create_user);
			st.setString(1, id);
			ResultSet rs = st.executeQuery();

			
			while (rs.next()) {
				String token =  rs.getString("googledrivetoken")+"";
				conn.close();
				return token;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}


	public void close() throws SQLException {
		conn.close();
	}


}
