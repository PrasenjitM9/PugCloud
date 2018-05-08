package com.droovy.request;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

@Path("request")
public class UserApiRequest {
	
	
	UserRequest request_dropbox = new UserRequestDropbox();
	UserRequest request_googledrive = new UserRequestGoogleDrive();
	UserRequest request_onedrive = new UserRequestOneDrive();

	@GET
	@Produces("text/plain")
	@Path("/list")
	public String getFilesList(@Context UriInfo uriInfo,@QueryParam("path") String path,@QueryParam("idUser") String idUser,@QueryParam("idFolder") String idFolder) throws JsonProcessingException {
		
		//TO DO : Merge les sources et fusionner si fichier identique
		
		List<File> listDropbox, listGoogleDrive,listOneDrive;
		listDropbox = request_dropbox.getFilesList(path,idUser);
		listGoogleDrive = request_googledrive.getFilesList(idFolder,idUser);
		listOneDrive = request_onedrive.getFilesList(path, idUser);
		
		Merger merge = new Merger();
		
		List<File> mergedList = merge.merge(listDropbox, listGoogleDrive,listOneDrive);
		
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
	

	
	@POST
	@Produces("text/plain")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/upload")
	public String uploadFile(@FormDataParam("file") InputStream uploadInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail, @QueryParam("idUser") String idUser, @QueryParam("drive") String drive) throws IOException {
	
		OutputStream outputStream = new FileOutputStream(new java.io.File(fileDetail.getFileName()));
	
		int read = 0;
		byte[] bytes = new byte[150000000];
	
		while ((read = uploadInputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}

		outputStream.close();
		uploadInputStream.close();
		
		System.out.println("upload ");
		
		request_dropbox.uploadFile(fileDetail.getFileName(), "/test/"+fileDetail.getFileName(), idUser);
		
		System.out.println("upload ");

		return fileDetail.getFileName()+" "+fileDetail.getSize()+" "+fileDetail.getType()+" "+fileDetail;
	}

	@GET
	@Produces("text/plain")
	@Path("/delete")
	public String uploadFile( @QueryParam("idUser") String idUser, @QueryParam("path") String path, @QueryParam("idFile") String idFile,@QueryParam("drive") String drive) throws IOException {
		
		if(drive.equals("dropbox")) {
			request_dropbox.removeFile(idFile, path, idUser);
		}
		else if(drive.equals("onedrive")) {
			request_onedrive.removeFile(idFile, path, idUser);
		}
		else if(drive.equals("googledrive")) {
			request_googledrive.removeFile(idFile, path, idUser);
		}
		return "";
	}
	
	
}
