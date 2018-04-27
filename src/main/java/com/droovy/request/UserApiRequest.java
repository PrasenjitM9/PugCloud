package com.droovy.request;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("request")
public class UserApiRequest {

	@GET
	@Produces("text/plain")
	@Path("/googledrive")
	public String getFileGoogleDrive(@Context UriInfo uriInfo,@QueryParam("code") String code){
		System.out.println("recu");
		UserRequest requestGoogleDrive = new UserRequestGoogleDrive();
		requestGoogleDrive.getFilesList();
		return "";
		
	}
	
	@GET
	@Produces("text/plain")
	@Path("/list")
	public String getFilesList(@Context UriInfo uriInfo,@QueryParam("password") String mdp,@QueryParam("id") String id) {
		return new UserRequestDropbox().getFilesList();
	}
	
	
	
}
