package com.droovy.request;

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
	public boolean removeFile(String id_file, String id) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String addFile(String filename, String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
