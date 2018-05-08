package com.droovy.request;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.MultiPartMediaTypes;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;

import com.droovy.DatabaseOp;
import com.droovy.JSONParser.JSONParser;
import com.droovy.JSONParser.JSONParserDropbox;
import com.droovy.JSONParser.JSONParserGoogledrive;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UserRequestDropbox implements UserRequest{

	@Override
	public List<File> getFilesList(String path,String id) {

		String url = "https://api.dropboxapi.com/2/files/list_folder";
		JSONParser parser = new JSONParserDropbox();


		try{

			if(path.equals("root")) {
				path="";
			}
			
			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			String jsonData = "{\"path\": \""+path+"\",\"recursive\": false,\"include_media_info\": false,\"include_deleted\": false,\"include_has_explicit_shared_members\": false,\"include_mounted_folders\": true }";

			DatabaseOp db = new DatabaseOp();

			Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserDropBoxToken(id)).header("Content-Type", "application/json").accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		

			String output =  response.readEntity(String.class);

			return parser.parserFiles(output);		

		}catch(Exception e){
			e.printStackTrace();
		}

		return new LinkedList<>();

	}


	@Override
	public boolean removeFile(String idFile,String path, String idUser) {
		String url = "https://api.dropboxapi.com/2/files/delete_v2";
		
		try{
			
			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			jerseyClient.register(MultiPartFeature.class);
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			String jsonData = "{\"path\": \""+path+"\"}";
			
			DatabaseOp db = new DatabaseOp();
			
		    Response response = jerseyTarget.request().header("Content-Type", "application/json").header("Authorization", "Bearer "+db.getUserDropBoxToken(idUser)).accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		
			return true;		

		}catch(Exception e){
			e.printStackTrace();
		}

		return false;
	}


	@Override
	public boolean uploadFile(String pathToFile, String pathInDrive,String userId) {
		String url = "https://content.dropboxapi.com/2/files/upload";

		System.out.println("upload ");
		
		try{
			
			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			jerseyClient.register(MultiPartFeature.class);
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			String jsonData = "{\"path\": \""+pathInDrive+"\",\"mode\": \"add\",\"autorename\": true,\"mute\": false}";
			
			/*
			 * --header "Authorization: Bearer " \
    --header "Dropbox-API-Arg: {\"path\": \"/Homework/math/Matrices.txt\",\"mode\": \"add\",\"autorename\": true,\"mute\": false}" \
    --header "Content-Type: application/octet-stream" \
    --data-binary @local_file.txt
			 */
			
			DatabaseOp db = new DatabaseOp();

			java.io.File file = new java.io.File(pathToFile);
			
			
			byte[] fileData = new byte[(int) file.length()];
			FileInputStream in = new FileInputStream(file);
			in.read(fileData);
			in.close();
			System.out.println("file : "+fileData.toString());
		
			/*
		    FileDataBodyPart filePart = new FileDataBodyPart("file", 
		                                             new java.io.File(pathToFile));
		    
		    filePart.setContentDisposition(
		            FormDataContentDisposition.name("file")
		                                    .fileName(pathToFile).build());

		 */
			/*
			
			 MultiPart multiPart = new MultiPart();
		        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

		        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
		        		new java.io.File(pathToFile)
		            );
		        multiPart.bodyPart(fileDataBodyPart);*/ 
			System.out.println("uploading ");

		    Response response = jerseyTarget.request().header("Dropbox-API-Arg", jsonData).header("Content-Type", "application/octet-stream").header("Authorization", "Bearer "+db.getUserDropBoxToken(userId)).post(Entity.entity(fileData.toString(), MediaType.APPLICATION_OCTET_STREAM));
			
			System.out.println("uploaded ");

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		
			return true;		

		}catch(Exception e){
			e.printStackTrace();
		}

		return false;
	}



}
