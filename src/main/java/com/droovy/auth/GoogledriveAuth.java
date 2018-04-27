package com.droovy.auth;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.client.JerseyWebTarget;

import com.droovy.DatabaseOp;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("googledriveauth")
public class GoogledriveAuth implements Auth{
	
	
	//A SUPPRIMER 
	private String token;
	
	private String client_id = "783584831345-rpngg6uic1i0iorvp2l5agc9ajmdm64v.apps.googleusercontent.com";
	private String client_secret = "0VnkLfVVZlE3c5SGiBk5AP7p" ;

	private ObjectMapper objectMapper = new ObjectMapper();
	
    private String url = "https://www.googleapis.com/oauth2/v4/token";
    private String redirect_uri = "http://localhost:8080/droovy/googledriveauth/callback";
	

    
    @GET
    @Produces("text/plain")
    @Path("/callback")
    public String callBackAuth(@Context UriInfo uriInfo,@QueryParam("code") String code) throws JsonProcessingException, IOException {
    	

    	
    	System.out.println("callback receive");
    	
    	Client client = Client.create();
		
		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);
		JerseyInvocation.Builder jerseyInvocation = jerseyTarget.request("application/json");

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("client_id", client_id);
		formData.add("client_secret", client_secret);

		formData.add("code", code);
		formData.add("grant_type", "authorization_code");
		formData.add("redirect_uri", redirect_uri);

		Response response = jerseyTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.form(formData));

		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus()+ " "+ response.toString());
		}
		String output =  response.readEntity(String.class);
		
		
		JsonNode rootNode = objectMapper.readTree(output);
		JsonNode tokenNode = rootNode.path("access_token");
		
		DatabaseOp.updateUserGoogleDriveToken(tokenNode.asText());	
		
		System.out.println("Output from Server .... "+output+"\n");
		System.out.println(response.toString());
		

		return "Response : "+output;
		
    }

    
}
