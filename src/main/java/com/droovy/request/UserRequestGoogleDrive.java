package com.droovy.request;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;

import com.droovy.DatabaseOp;


public class UserRequestGoogleDrive implements UserRequest {

	@Override
	public String getFilesList() {
		String token = DatabaseOp.getUserGoogleDriveToken();
			
		String url = "https://www.googleapis.com/drive/v2/files";

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();

		Response response = jerseyTarget.request().header("Authorization","Bearer "+token).accept(MediaType.APPLICATION_JSON).post(Entity.form(formData));

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus()+ " "+ response.toString());
		}
		String output =  response.readEntity(String.class);
		System.out.println(output);

		return "Response : "+output;
	}

}
