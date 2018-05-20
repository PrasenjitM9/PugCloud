package com.droovy.request;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse.Status;

import errors.UserApplicationError;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

@Path("request")
public class UserApiRequest {
	
	
	UserRequest request_dropbox = new UserRequestDropbox();
	UserRequest request_googledrive = new UserRequestGoogleDrive();
	UserRequest request_onedrive = new UserRequestOneDrive();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list")
	public Response getFilesList(@Context UriInfo uriInfo,@QueryParam("path") String path,@QueryParam("idUser") String idUser,@QueryParam("idFolder") String idFolder,@QueryParam("getDropbox") int getDropbox,@QueryParam("getGoogleDrive") int getGoogledrive,@QueryParam("getOnedrive") int getOnedrive,@QueryParam("folderOnly") String onlyFolders) throws JsonProcessingException {
		
		boolean folderOnly = onlyFolders.equals("true");
		
		if(idUser == null || path==null || idFolder == null) {
			throw new UserApplicationError("At least one argument is missing", 400);
		}
		
		List<File> listDropbox = new LinkedList<>(), listGoogleDrive = new LinkedList<>(),listOneDrive = new LinkedList<>();
		
		if(getDropbox==1) {			
			listDropbox = request_dropbox.getFilesList(path,idUser,folderOnly);
		}
		if(getGoogledrive==1) {
			listGoogleDrive = request_googledrive.getFilesList(idFolder,idUser,folderOnly);
		}
		if(getOnedrive==1) {
			listOneDrive = request_onedrive.getFilesList(path, idUser,folderOnly);
		}
		
		Merger merge = new Merger();
		List<File> mergedList = merge.merge(listGoogleDrive, listDropbox, listOneDrive);
		
		ObjectMapper mapper = new ObjectMapper();
		
		String output = "[";
		boolean atleastOneFolder = false;
		if(folderOnly){
			for (File file : mergedList) {
				if(file.getType()==FileType.FOLDER) {
					atleastOneFolder=true;
					output = output + mapper.writeValueAsString(file)+",";
				}
			}
			if(!atleastOneFolder) {
				output += "]";
			}
			else {
				output = output.substring(0,output.length()-1);//Retire la virgule en trop
				output += "]";
			}
		}
		else {
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
		}
		
		
		
		return Response.status(Status.OK).entity(output).build();
	}
	

	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/upload")
	public Response uploadFile(@FormDataParam("file") InputStream uploadInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail, @FormDataParam("idUser") String idUser, @FormDataParam("drive") String drive,@FormDataParam("pathInDrive") String pathInDrive,@FormDataParam("parentId") String parentId) throws IOException {
	
		if(idUser == null || uploadInputStream==null || fileDetail == null || drive == null || parentId == null || pathInDrive == null ) {
			throw new UserApplicationError("At least one argument is missing", 400);
		}
		
		OutputStream outputStream = new FileOutputStream(new java.io.File(fileDetail.getFileName()));
		
		/*Sockage du fichier en local => voir si peut pas utiliser directement l'input stream*/
		int read = 0;
		byte[] bytes = new byte[1024];
	
		while ((read = uploadInputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}

		outputStream.close();
		uploadInputStream.close();		
		
		File uploadedFile;
		
		if(drive.equals("dropbox")) {
			uploadedFile=request_dropbox.uploadFile(fileDetail.getFileName(),pathInDrive, idUser,parentId);
		}
		else if(drive.equals("onedrive")) {
			uploadedFile=request_onedrive.uploadFile(fileDetail.getFileName(), pathInDrive, idUser,parentId);
		}
		else if(drive.equals("googledrive")) {
			uploadedFile=request_googledrive.uploadFile(fileDetail.getFileName(), pathInDrive, idUser,parentId);
		}
		else {
			throw new UserApplicationError("Tell in which drive upload, example : drive=dropbox", 400);
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(uploadedFile);
		
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(output).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/delete")
	public Response deleteFile( @QueryParam("idUser") String idUser, @QueryParam("path") String path, @QueryParam("idFile") String idFile,@QueryParam("drive") String drive) throws IOException {
		
		if(idUser == null || path==null || idFile == null || drive == null ) {
			throw new UserApplicationError("At least one argument is missing", 400);
		}
		
		
		if(drive.equals("dropbox")) {
			request_dropbox.removeFile(idFile, path, idUser);
		}
		else if(drive.equals("onedrive")) {
			request_onedrive.removeFile(idFile, path, idUser);
		}
		else if(drive.equals("googledrive")) {
			request_googledrive.removeFile(idFile, path, idUser);
		}
		else {
			throw new UserApplicationError("Tell in which drive upload, example : drive=dropbox", 400);
		}
		
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity("{\"success\" : \"ok\"}").build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/rename")
	public Response renameFile( @QueryParam("idUser") String idUser, @QueryParam("path") String path, @QueryParam("idFile") String idFile,@QueryParam("drive") String drive, @QueryParam("name") String name) throws IOException {

		File renamedFile;
		
		if(idUser == null || path==null || idFile == null || drive == null || name == null ) {
			throw new UserApplicationError("At least one argument is missing", 400);
		}
		
		
		if(drive.equals("dropbox")) {
			renamedFile=request_dropbox.renameFile(idFile, path, name, idUser);
		}
		else if(drive.equals("onedrive")) {
			renamedFile=request_onedrive.renameFile(idFile, path, name, idUser);
		}
		else if(drive.equals("googledrive")) {
			renamedFile=request_googledrive.renameFile(idFile, path, name, idUser);
		}
		else {
			throw new UserApplicationError("Tell in which drive upload, example : drive=dropbox", 400);
		}
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(renamedFile);
			
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(output).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/move")
	public Response moveFile( @QueryParam("idUser") String idUser, @QueryParam("path") String path, @QueryParam("idFile") String idFile,@QueryParam("drive") String drive, @QueryParam("idParent") String idParent, @QueryParam("pathParent") String pathParent, @QueryParam("name") String name) throws IOException {

		File movedFile;

		if(idUser == null || path==null || idFile == null || drive == null || name == null || pathParent == null ) {
			throw new UserApplicationError("At least one argument is missing", 400);
		}
		
		
		if(drive.equals("dropbox")) {
			movedFile = request_dropbox.moveFile(idFile, path, idParent, pathParent, idUser,name);
		}
		else if(drive.equals("onedrive")) {
			movedFile = request_onedrive.moveFile(idFile, path, idParent, pathParent, idUser,name);
		}
		else if(drive.equals("googledrive")) {
			movedFile = request_googledrive.moveFile(idFile, path, idParent, pathParent, idUser,name);
		}
		else {
			throw new UserApplicationError("Tell in which drive upload, example : drive=dropbox", 400);
		}
		ObjectMapper mapper = new ObjectMapper();
		String output = mapper.writeValueAsString(movedFile);
			
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(output).build();
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/freespace")
	public Response freeSpace( @QueryParam("idUser") String idUser, @QueryParam("drive") String drive) throws IOException {
		
		if(idUser == null || drive == null ) {
			throw new UserApplicationError("At least one argument is missing", 400);
		}
		
		String output;
		
		if(drive.equals("dropbox")) {
			output=request_dropbox.freeSpaceRemaining(idUser);
		}
		else if(drive.equals("onedrive")) {
			output=request_onedrive.freeSpaceRemaining(idUser);
		}
		else if(drive.equals("googledrive")) {
			output=request_googledrive.freeSpaceRemaining(idUser);
		}
		else {
			throw new UserApplicationError("Tell in which drive upload, example : drive=dropbox", 400);
		}
		
		return Response.status(Status.OK).type(MediaType.APPLICATION_JSON).entity(output).build();
			
	}
	
	/*@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/share")
	public Response shareFile(@QueryParam("folder") String folder,@QueryParam("permission") String permission, @QueryParam("message") String message,@QueryParam("idUser") String idUser,@QueryParam("idFile") String idFile,@QueryParam("mail") String mail){
				
		FilePermission permissionFile = FilePermission.READ;
		if(permission.equals("read")) {
			permissionFile = FilePermission.WRITE;
		}
		
		
		request_dropbox.shareFile(idUser, message, idFile, mail,permissionFile,(folder.equals("1")) ? true : false);
		
		return Response.status(Status.OK).entity("").build();
		
	}*/
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/search")
	public Response searchFile(@QueryParam("idUser") String idUser,@QueryParam("query") String query,@QueryParam("getDropbox") int getDropbox,@QueryParam("getGoogleDrive") int getGoogledrive,@QueryParam("getOnedrive") int getOnedrive) throws JsonProcessingException{
	
		if(idUser == null || query == null ) {
			throw new UserApplicationError("At least one argument is missing", 400);
		}
		
		List<File> listDropbox = new LinkedList<>(), listGoogleDrive = new LinkedList<>(),listOneDrive = new LinkedList<>();
		
		if(getDropbox==1) {			//Transformer en fonction ou hashmap qui regarde si token null
			listDropbox = request_dropbox.searchFile(idUser,query);
		}
		if(getGoogledrive==1) {
			listGoogleDrive = request_googledrive.searchFile(idUser,query);
		}
		if(getOnedrive==1) {
			listOneDrive = request_onedrive.searchFile(idUser,query);
		}
		
		listDropbox.addAll(listOneDrive);
		listDropbox.addAll(listGoogleDrive);
		
		ObjectMapper mapper = new ObjectMapper();
		
		String output = "[";

		for (File file : listDropbox) {
			
			output = output + mapper.writeValueAsString(file)+",";
		}

		if(listDropbox.isEmpty()) {
			output += "]";
		}
		else {
			output = output.substring(0,output.length()-1);//Retire la virgule en trop
			output += "]";
		}
		
		return Response.status(Status.OK).entity(output).build();
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/download")
	public Response searchFile(@QueryParam("fileName") String fileName,@QueryParam("idUser") String idUser,@QueryParam("idFile") String idFile, @QueryParam("drive") String drive) {
	
		final java.io.File fileToSend;
		if(drive.equals("dropbox")) {
			fileToSend = request_dropbox.downloadFile(idUser, idFile);
		}
		else if(drive.equals("onedrive")) {
			fileToSend = request_onedrive.downloadFile(idUser, idFile);
		}
		else if(drive.equals("googledrive")) {
			fileToSend = request_googledrive.downloadFile(idUser, idFile);
		}
		else {
			throw new UserApplicationError("Tell in which drive, example : drive=dropbox", 400);
		}

		 StreamingOutput fileStream =  new StreamingOutput()
	        {
	            @Override
	            public void write(java.io.OutputStream output) throws IOException, WebApplicationException
	            {
	                try
	                {
	                    java.nio.file.Path path = Paths.get(fileToSend.getAbsolutePath());
	                    byte[] data = Files.readAllBytes(path);
	                    output.write(data);
	                    output.flush();
	                }
	                catch (Exception e)
	                {
	                    throw new WebApplicationException("File Not Found");
	                }
	            }
	        };
	        return Response
	                .ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
	                .header("content-disposition","attachment; filename = "+fileName)
	                .build();

	}
}
