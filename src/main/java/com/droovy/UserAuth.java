package com.droovy;


import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;


@Path("user")
public class UserAuth {


	@GET
	@Produces("text/plain")
	@Path("/create")
	public String userCreate(@Context UriInfo uriInfo,@QueryParam("password") String mdp,@QueryParam("id") String id) {
		
		boolean creationSuccess = true;
		int idClient = -1;
		
		/**
		 * TO DO : 
		 * create user account with id and mdp
		 * 
		 */
		
		  idClient = 4;
		 /**---*/
		
		 if(creationSuccess) {
				return "{\"id\" : \""+idClient+"\""
						+ "\"success\" : \"true\"}";
		 }
		 else {
			 	return "{\"success\" : \"false\"}";
		 }
	}



	@GET
	@Produces("text/plain")
	@Path("/auth")
	public String userAuth(@Context UriInfo uriInfo,@QueryParam("password") String mdp,@QueryParam("id") String id) {

		
		 boolean connexionSuccess = false;
		 int idClient = -1;
		 
		 /**
		  * TO DO :
		  * Check an get cuser id in database
		  */
		
		 connexionSuccess = true;
		 idClient = 4;
		 
		 /**----*/
		 
		 
		 if(connexionSuccess) {
				return "{\"id\" : \""+idClient+"\""
						+ "\"success\" : \"true\"}";
		 }
		 else {
			 	return "{\"success\" : \"false\"}";
		 }
	}


	public static void updateUserDatabase(String token) {

		String url = "./" + "users.sqlite";

		File file = new File(url);


		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+url)) {
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();



			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
