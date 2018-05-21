package com.droovy;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.droovy.auth.GoogledriveAuth;
import com.droovy.auth.OneDriveAuth;
import com.fasterxml.jackson.core.JsonProcessingException;

import errors.InternalServerError;
import errors.UserApplicationError;

public class DatabaseOp {

	//private Connection conn;

	private static String url = "./" + "users.db";

	public static final long HOUR = 3600*1000;

	private static String sql_create_table = "CREATE TABLE IF NOT EXISTS users (\n"
			+ "	id integer PRIMARY KEY AUTOINCREMENT,\n"
			+ "	name TEXT NOT NULL,\n"
			+ "	password TEXT NOT NULL,\n"
			+ " googledrivetoken TEXT,\n"
			+ " dropboxtoken TEXT,\n"
			+ " onedrivetoken TEXT,\n"
			+ " googledrivetokenrefresh TEXT,\n"
			+ " onedrivetokenrefresh TEXT,\n"
			+ " googledrivetokencreation TEXT,\n"
			+ " onedrivetokencreation TEXT\n"
			+ ");";



	private static Connection c = null;


	private static Connection connectDb() {
		
		if(c == null){
			
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:"+url);
				c.createStatement().executeUpdate(sql_create_table);
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					@Override
					public void run() {
						close();
					}
				}));

			}
			catch(Exception e) {
				throw new InternalServerError();
			}
			
		}
		return c;
	}


	public int createUser(String name, String password){

		String sql_create_user = "INSERT INTO users (name,password) values (?,?);";

		String sql_get_user_id = "SELECT MAX(id) as id FROM users";

		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_create_user);
			st.setString(1, name);
			st.setString(2, password);
			st.executeUpdate();


			ResultSet rs = connectDb().createStatement().executeQuery(sql_get_user_id);

			while (rs.next()) {
				return rs.getInt("id");
			}
			return -1;

		} catch (Exception e) {
			throw new InternalServerError();
		}

	}

	public boolean checkIfUserExist(String name) {
		
		
		String sql_get_user = "SELECT id FROM users WHERE name == ?";

		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_get_user);
			st.setString(1, name);
			st.execute();


			ResultSet rs = st.getResultSet();

			while (rs.next()) {

				return true;
			}
			return false;

		} catch (Exception e) {
			throw new InternalServerError();
		}

	}


	public int authUser(String name, String password){
		
		

		String sql_get_user = "SELECT id FROM users WHERE name == ? and password == ?";

		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_get_user);
			st.setString(1, name);
			st.setString(2, password);
			st.execute();


			ResultSet rs = st.getResultSet();

			while (rs.next()) {
				int id =  rs.getInt("id");

				return id;
			}
			return -1;

		} catch (Exception e) {
			throw new InternalServerError();
		}


	}
	
	public boolean checkIfExist(String id) {
		
		
		String sql_get_user = "SELECT name FROM users WHERE id == ?";

		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_get_user);
			st.setString(1, id);
			st.execute();


			ResultSet rs = st.getResultSet();

			while (rs.next()) {
				return true;
			}
			return false;

		} catch (Exception e) {
			throw new InternalServerError();
		}
	}

	public boolean updateUserOneDriveToken(String token,String refreshToken,String id){
		
		if(!checkIfExist(id)) {
			throw new UserApplicationError(442);
		}
		
		String sql_update_user = "UPDATE users SET onedrivetoken = ?, onedrivetokencreation = datetime('now','localtime'), onedrivetokenrefresh = ? WHERE id == ?;";


		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_update_user);
			st.setString(1, token);
			st.setString(2, refreshToken);
			st.setString(3, id);
			st.executeUpdate();



			return true;

		} catch (Exception e) {
			throw new InternalServerError();
		}
	}

	public boolean updateUserDropBoxToken(String token,String id){
		
		if(!checkIfExist(id)) {
			throw new UserApplicationError(442);
		}
		
		String sql_update_user = "UPDATE users SET dropboxtoken = ? WHERE id == ?;";


		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_update_user);
			st.setString(1, token);
			st.setString(2, id);
			st.executeUpdate();

			return true;

		} catch (Exception e) {
			throw new InternalServerError();
		}	
	}

	public boolean updateUserGoogleDriveToken(String token,String refreshToken, String id){
		
		if(!checkIfExist(id)) {
			throw new UserApplicationError(442);
		}
		
		String sql_update_user = "UPDATE users SET googledrivetoken = ?, googledrivetokencreation = datetime('now','localtime') ,googledrivetokenrefresh = ? WHERE id == ?;";

		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_update_user);
			st.setString(1, token);
			st.setString(2, refreshToken);
			st.setString(3, id);
			st.executeUpdate();

			return true;

		} catch (Exception e) {
			throw new InternalServerError();
		}
	}





	public String getUserOneDriveToken(String id) {
		
		if(!checkIfExist(id)) {
			throw new UserApplicationError(442);
		}
		
		String sql_create_user = "SELECT onedrivetoken,onedrivetokencreation,onedrivetokenrefresh FROM users WHERE id == ?;";


		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_create_user);
			st.setString(1, id);
			ResultSet rs = st.executeQuery();


			while (rs.next()) {
				String token =  rs.getString("onedrivetoken");

				DateFormat format = new SimpleDateFormat("y-M-d h:m:s", Locale.FRANCE);
				String date = rs.getString("onedrivetokencreation");

				if(date!=null) {

					Date dateCreation;
					try {
						dateCreation = format.parse(date);
					} catch (Exception e) {
						return "";
					}

					Date dateExpiration = new Date(dateCreation.getTime() + 1 * HOUR);
					
					if(dateExpiration.compareTo(new Date()) < 0 ) {

						try {
							return new OneDriveAuth().refreshToken(rs.getString("onedrivetokenrefresh"),id);
						} catch (Exception e) {
							throw new InternalServerError();
						}

					}
				}

				return token;
			}

		} catch (Exception e) {
			throw new InternalServerError();
		}
		return "";
	}

	public String getUserDropBoxToken(String id){
		
		if(!checkIfExist(id)) {
			throw new UserApplicationError(442);
		}
		
		String sql_create_user = "SELECT dropboxtoken FROM users WHERE id == ?;";


		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_create_user);
			st.setString(1, id);
			ResultSet rs = st.executeQuery();


			while (rs.next()) {
				String token =  rs.getString("dropboxtoken");


				return token;
			}

		} catch (Exception e) {
			throw new InternalServerError();
		}
		return "";
	}

	public String getUserGoogleDriveToken(String id) {
		
		if(!checkIfExist(id)) {
			throw new UserApplicationError(442);
		}
		
		String sql_create_user = "SELECT googledrivetoken,googledrivetokencreation,googledrivetokenrefresh FROM users WHERE id == ?;";

		try {
			
			PreparedStatement st = connectDb().prepareStatement(sql_create_user);
			st.setString(1, id);
			ResultSet rs = st.executeQuery();


			while (rs.next()) {
				String token =  rs.getString("googledrivetoken");

				DateFormat format = new SimpleDateFormat("y-M-d h:m:s", Locale.FRANCE);

				String date = rs.getString("googledrivetokencreation");
				if(date!=null) {
					
					Date dateCreation;
					try {
						dateCreation = format.parse(date);
					} catch (ParseException e1) {
						throw new InternalServerError();
					}

					Date dateExpiration = new Date(dateCreation.getTime() + 1 * HOUR);

					if(dateExpiration.compareTo(new Date()) < 0 ) {		
						System.out.println("refresh");
						try {
							return new GoogledriveAuth().refreshToken(rs.getString("googledrivetokenrefresh"),id);
						} catch (Exception e) {
							throw new InternalServerError();
						}					
					}

				}
				return token;
			}

		} catch (Exception e) {
			throw new InternalServerError();
		}
		return "";
	}


	public static void close() {
		try {
			c.close();
		} catch (SQLException e) {
			throw new InternalServerError();
		}
	}


}
