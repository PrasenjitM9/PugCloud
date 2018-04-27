package com.droovy.request;

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
	public String getFilesList(String path) {
		
		try{

			String url = "https://www.googleapis.com/drive/v2/files";

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
			Response response = jerseyTarget.request().header("Authorization", "Bearer "+DatabaseOp.getUserGoogleDriveToken()).accept(MediaType.APPLICATION_JSON).get();

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		
			String output =  response.readEntity(String.class);
			
			List<File> listFile = parser.parserFiles((output));
			
			ObjectMapper mapper = new ObjectMapper();
			
			String json = "";
			for (File file : listFile) {
				json = json + mapper.writeValueAsString(file);
			}

			return json;

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "-1";
	}


}
