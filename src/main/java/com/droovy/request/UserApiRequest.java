package com.droovy.request;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("request")
public class UserApiRequest {
	
	
	UserRequest request_dropbox = new UserRequestDropbox();
	UserRequest request_googledrive = new UserRequestGoogleDrive();
	UserRequest request_onedrive = new UserRequestOneDrive();

	@GET
	@Produces("text/plain")
	@Path("/list")
	public String getFilesList(@Context UriInfo uriInfo,@QueryParam("path") String path,@QueryParam("id") String id) throws JsonProcessingException {
		
		//TO DO : Merge les sources et fusionner si fichier identique
		
		List<File> listDropbox, listGoogleDrive,listOneDrive;
		listDropbox = request_dropbox.getFilesList(path,id);
		listGoogleDrive = request_googledrive.getFilesList(path,id);
		
		Merger merge = new Merger();
		
		List<File> mergedList = merge.merge(listDropbox, listGoogleDrive);
		
		ObjectMapper mapper = new ObjectMapper();
		
		String output = "[";

		for (File file : mergedList) {
			
			output = output + mapper.writeValueAsString(file)+",";
		}

		if(mergedList.isEmpty()) {
			output += "]";
		}
		else {
			output = output.substring(0,output.length()-1);//Retire la virgule en trop
			output += "]";
		}
		
		return output; 
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
