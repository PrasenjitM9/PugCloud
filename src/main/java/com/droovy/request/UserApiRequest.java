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
	@Path("/list")
	public String getFilesList(@Context UriInfo uriInfo,@QueryParam("path") String path,@QueryParam("id") String id) {
		
		System.out.println("Path ; "+path);
		
		return new UserRequestDropbox().getFilesList(path)+new UserRequestGoogleDrive().getFilesList(path);
	}
	
	
	
}
