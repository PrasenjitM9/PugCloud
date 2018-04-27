package com.droovy.request;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.server.ResourceConfig;

import com.droovy.DatabaseOp;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class UserRequestGoogleDrive implements UserRequest{

	@Override
	public String getFilesList(String path) {

		try{

			String url = "https://www.googleapis.com/drive/v2/files";

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
			Response response = jerseyTarget.request().header("Authorization", "Bearer "+DatabaseOp.getUserGoogleDriveToken()).accept(MediaType.APPLICATION_JSON).post(Entity.form(formData));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		JerseyInvocation.Builder jerseyInvocation = jerseyTarget.request("application/json");

			String output =  response.readEntity(String.class);


			System.out.println("Files from Server .... "+output+"\n");
			System.out.println(response.toString());
			return "Response : "+output;


		}catch(Exception e){
			e.printStackTrace();
		}
		return "Response : ";
	}


}
