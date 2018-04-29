package com.droovy.auth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;

import com.droovy.DatabaseOp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.representation.Form;

@Path("onedriveauth")
public class OneDriveAuth implements Auth{
	
	
	private String client_id = "7da78622-f4f8-47d0-bbb0-9b777af993a7";
	private String client_secret = "ikxlBTRW137;czlEFX26?+=" ;

	private ObjectMapper objectMapper = new ObjectMapper();
	
    private String url = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
    private String redirect_uri = "http://localhost:8080/droovy/onedriveauth/callback/";
	

    
    @GET
    @Produces("text/plain")
    @Path("/callback")
    public Response callBackAuth(@Context UriInfo uriInfo,@QueryParam("code") String code,@QueryParam("state") String state) throws JsonProcessingException, IOException, URISyntaxException {
      			
		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		Form formData = new Form();
		formData.add("client_id", client_id);
		formData.add("client_secret", client_secret);
		formData.add("scope", "user.read mail.read");
		formData.add("code", code);
		formData.add("grant_type", "authorization_code");
		formData.add("redirect_uri", redirect_uri);

		Response response = jerseyTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.form(formData));

		if (response.getStatus() != 200) {
			/*
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus()+ " "+ response.toString());*/
			return Response.temporaryRedirect(new URI("http://localhost:4200?success=false")).build();

		}
		else {
			String output =  response.readEntity(String.class);
			
			
			JsonNode rootNode = objectMapper.readTree(output);
			JsonNode tokenNode = rootNode.path("access_token");
			
			DatabaseOp db = new DatabaseOp();
			
			if(db.updateUserOneDriveToken(tokenNode.asText(),state)) {
				
				return Response.temporaryRedirect(new URI("http://localhost:4200?success=true")).build();
			}
			else {
				return Response.temporaryRedirect(new URI("http://localhost:4200?success=false")).build();
			}
			
			
		}
		
		
		
    }

    
}
