package com.droovy.errors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InternalServerError  extends WebApplicationException {
	
	  public InternalServerError(String message) {
	        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	        		.entity("{\"errorMessage\" : \""+message+"\", \"code\" : \"500\"}").type(MediaType.APPLICATION_JSON).build());
	  }
	  public InternalServerError() {
	        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	        		.entity("{\"errorMessage\" : \"An error occured on the server\", \"code\" : \"500\"}").type(MediaType.APPLICATION_JSON).build());
	  }
	  
}
