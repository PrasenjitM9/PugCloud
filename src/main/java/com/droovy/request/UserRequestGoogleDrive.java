package com.droovy.request;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.grizzly.http.server.util.Mapper;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.server.ResourceConfig;

import com.droovy.DatabaseOp;
import com.droovy.JSONParser.JSONParser;
import com.droovy.JSONParser.JSONParserGoogledrive;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class UserRequestGoogleDrive implements UserRequest{
	
	JSONParser parser = new JSONParserGoogledrive();

	@Override
	public List<File> getFilesList(String path,String id) {

		try{

			if(path.equals("root")) {
				path="";
			}
			else {
				path = "q="+path+" in parents";
			}
			
			String url = "https://www.googleapis.com/drive/v2/files?"+path;

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			DatabaseOp db = new DatabaseOp();
			Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(id)).accept(MediaType.APPLICATION_JSON).get();
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		
			String output =  response.readEntity(String.class);
			
			return parser.parserFiles((output));

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return new LinkedList<>();
	}

	@Override
	public String addFile(String filename, String id) {
		try{

			String url = "https://www.googleapis.com/drive/v2/files";

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
			formData.add("title:", filename);

			DatabaseOp db = new DatabaseOp();
			Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(id)).accept(MediaType.APPLICATION_JSON).post(Entity.text("title:test"));

						
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		

			return "1";

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "-1";
	}


	@Override
	public boolean removeFile(String id_file, String id) {
		// TODO Auto-generated method stub
		return false;
	}
}
