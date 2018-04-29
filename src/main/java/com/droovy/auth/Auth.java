package com.droovy.auth;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Auth {

	public Response callBackAuth(@Context UriInfo uriInfo,@QueryParam("code") String code,@QueryParam("state") String state) throws JsonProcessingException, IOException, URISyntaxException;

}
