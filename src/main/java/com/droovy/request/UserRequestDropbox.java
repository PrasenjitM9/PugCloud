package com.droovy.request;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.client.JerseyWebTarget;

import com.droovy.DatabaseOp;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class UserRequestDropbox implements UserRequest {

	@Override
	public String getFilesList() {
		
		DatabaseOp.updateUserDropBoxToken("senU5G790IAAAAAAAAAArfc5TEtCNrjYE6dR_AhHa7MVnVub9BgzDlHb0gmwNHFY");
		
		String url = "https://api.dropboxapi.com/2/files/list_folder";

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);
		
	
		String json = "{\"path\": \"/test\",\"recursive\": false,\"include_media_info\": false,\"include_deleted\": false,\"include_has_explicit_shared_members\": false,\"include_mounted_folders\": true }";
		
		
		Response response = jerseyTarget.request().header("Authorization", "Bearer "+DatabaseOp.getUserDropBoxToken()).header("Content-Type", "application/json").header("data", "{\"path\": \"/test\",\"recursive\": false,\"include_media_info\": false,\"include_deleted\": false,\"include_has_explicit_shared_members\": false,\"include_mounted_folders\": true }").accept(MediaType.APPLICATION_JSON).post(Entity.json(json));
		
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus()+ " "+ response.toString());
		}		

		String output =  response.readEntity(String.class);
	
		
		System.out.println("Files from Server .... "+output+"\n");
		System.out.println(response.toString());
		

		return "Response : "+output;
		
		
		
		
	}

}
