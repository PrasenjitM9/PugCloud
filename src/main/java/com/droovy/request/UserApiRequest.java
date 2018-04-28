package com.droovy.request;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("request")
public class UserApiRequest {
	
	
	UserRequest request_dropbox = new UserRequestDropbox();
	UserRequest request_googledrive = new UserRequestGoogleDrive();
	UserRequest request_onedrive = new UserRequestOneDrive();

	@GET
	@Produces("text/plain")
	@Path("/list")
	public String getFilesList(@Context UriInfo uriInfo,@QueryParam("path") String path,@QueryParam("id") String id) {		
		return request_dropbox.getFilesList(path,id)+request_googledrive.getFilesList(path,id);
	}
	
	@GET
	@Produces("text/plain")
	@Path("/add")
	public String addFile(@Context UriInfo uriInfo,@QueryParam("filename") String filename,
			@QueryParam("googledrive") boolean googledrive,@QueryParam("dropbox") boolean dropbox,
			@QueryParam("onedrive") boolean onedrive, @QueryParam("id") String id) {
		
		String result = "";
		if(googledrive){
			result += request_googledrive.addFile(filename, id);
		}
		if(dropbox){
			result += request_dropbox.addFile(filename, id);
		}
		if(onedrive){
			result += request_onedrive.addFile(filename, id);
		}
		return result;
	}
	
	
	
}
