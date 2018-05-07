package com.droovy;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

public class CORSResponse implements ContainerResponseFilter {

	
		@Override
		public void filter(ContainerRequestContext arg0, ContainerResponseContext arg1) throws IOException {
			MultivaluedMap<String, Object> headers = arg1.getHeaders();

			headers.add("Access-Control-Allow-Origin", "*");
			headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");			
			headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Codingpedia");
		}

	
}
