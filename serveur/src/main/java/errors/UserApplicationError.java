package errors;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserApplicationError extends WebApplicationException {
	

    public UserApplicationError(String message,int code) {
        super(Response.status(code)
        		.entity("{\"errorMessage\" : \""+message+"\", \"code\" : \""+code+"\"}").type(MediaType.APPLICATION_JSON).build());
    }
    public UserApplicationError(int code) {
    	    	    	
        super(Response.status(code)
        		.entity("{\"errorMessage\" : \"Bad request\", \"code\" : \""+code+"\"}").type(MediaType.APPLICATION_JSON).build());
    }
}
