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
		formData.add("scope", "Files.Read Files.ReadWrite Files.Read.All Files.ReadWrite.All Sites.Read.All Sites.ReadWrite.All");
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
			JsonNode tokenRefreshNode = rootNode.path("refresh_token");

			
			
			DatabaseOp db = new DatabaseOp();
			
			if(db.updateUserOneDriveToken(tokenNode.asText(),tokenRefreshNode.asText(),state)) {
				
				return Response.temporaryRedirect(new URI("http://localhost:4200/manager?success=true")).build();
			}
			else {
				return Response.temporaryRedirect(new URI("http://localhost:4200/manager?success=false")).build();
			}
			
			
		}
		
		
		
    }



	@Override
	public String refreshToken(String refreshToken, String idUser) throws JsonProcessingException, IOException {
		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target("https://login.live.com/oauth20_token.srf");

		Form formData = new Form();
		formData.add("client_id", client_id);
		formData.add("client_secret", client_secret);
		formData.add("grant_type", "refresh_token");
		formData.add("redirect_uri", redirect_uri);
		formData.add("refresh_token", refreshToken);

		Response response = jerseyTarget.request().header("Content-Type", "application/x-www-form-urlencoded").accept(MediaType.APPLICATION_JSON).post(Entity.form(formData));

		if (response.getStatus() != 200) {
			
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus()+ " "+ response.toString());

		}
		else {
			String output =  response.readEntity(String.class);
			
			
			JsonNode rootNode = objectMapper.readTree(output);
			JsonNode tokenNode = rootNode.path("access_token");
			JsonNode tokenRefreshNode = rootNode.path("refresh_token");

			
			
			DatabaseOp db = new DatabaseOp();
			
			db.updateUserOneDriveToken(tokenNode.asText(),tokenRefreshNode.asText(),idUser);
				
			System.out.println("new token one drive : "+tokenNode.asText());
			
			return tokenNode.asText();
		}
	}


    
}
