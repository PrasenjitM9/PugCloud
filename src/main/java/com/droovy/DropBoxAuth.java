package com.droovy;

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
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("dropboxauth")
public class DropBoxAuth{
	

	/**
	 * Method handling HTTP GET requests. The returned object will be sent
	 * to the client as "text/plain" media type.
	 *
	 * @return String that will be returned as a text/plain response.
	 */



	@GET
	@Produces("text/plain")
	@Path("/callback")
	public String callBackAuth(@Context UriInfo uriInfo,@QueryParam("code") String code) {


		String client_id = "i90y72ofs47u9b8";
		String client_secret = "7tvoiqp2ivspl7y";

		System.out.println("callback receive");

		String url = "https://api.dropboxapi.com/oauth2/token";

		String redirect_uri = "http://localhost:8080/droovy/dropboxauth/callback";

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
		formData.add("client_id", client_id);
		formData.add("client_secret", client_secret);

		formData.add("code", code);
		formData.add("grant_type", "authorization_code");
		formData.add("redirect_uri", redirect_uri);

		Response response = jerseyTarget.request().accept(MediaType.APPLICATION_JSON).post(Entity.form(formData));



		System.out.println(response.toString());


		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus()+ " "+ response.toString());
		}
		
		String output =  response.readEntity(String.class);
		System.out.println("Output from Server .... "+output+"\n");
		System.out.println(response.toString());

		return "Response : "+output;

	}


}
