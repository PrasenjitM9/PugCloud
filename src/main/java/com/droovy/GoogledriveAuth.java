package com.droovy;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.client.JerseyWebTarget;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("googledriveauth")
public class GoogledriveAuth {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    
    @GET
    @Produces("text/plain")
    @Path("/callback")
    public String callBackAuth(@QueryParam("code") String code) {
    	
    	String client_id = "783584831345-rpngg6uic1i0iorvp2l5agc9ajmdm64v.apps.googleusercontent.com";
    	String client_secret = "0VnkLfVVZlE3c5SGiBk5AP7p" ;
    	
    	System.out.println("callback receive");
    	
    	
    	Client client = Client.create();

        String url = "https://www.googleapis.com/oauth2/v4/token";
        
        String redirect_uri = "http:localhost:8080/googledriveauth/tokenreceiver";
		
		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);
		JerseyInvocation.Builder jerseyInvocation = jerseyTarget.request("application/json");
		
		jerseyInvocation.header("Context-type", "application/json");
		jerseyInvocation.header("code", code);
		jerseyInvocation.header("client_id",client_id);
		jerseyInvocation.header("client_secret", client_secret);
		jerseyInvocation.header("grant_type", "authorization_code");
		jerseyInvocation.header("redirect_uri", redirect_uri);

		Response response = jerseyInvocation.post(Entity.entity("", MediaType.APPLICATION_JSON), Response.class);
		
		System.out.println(response.toString());
		
		
/*
		if (response.getStatus() != 200) {
		   throw new RuntimeException("Failed : HTTP error code : "
			+ response.getStatus());
		}

		String output = (String) response.getEntity();

		System.out.println("Output from Server .... \n");
		System.out.println(output);

*/    	
    	
        return "Code : "+code;
    }
    
    @GET
    @Path("/tokenreceiver")
    public String toto(@QueryParam("code") String code) {
    	return "toto";
    }
    
}
