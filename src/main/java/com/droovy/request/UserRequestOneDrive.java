package com.droovy.request;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.droovy.DatabaseOp;
import com.droovy.JSONParser.JSONParser;
import com.droovy.JSONParser.JSONParserOneDrive;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.LoggingFilter;

public class UserRequestOneDrive implements UserRequest {

	@Override
	public List<File> getFilesList(String path,String id) {
		
		try{
			if(!path.equals("root")) {
				path=":"+path+":";
			}
			else {
				path="";
			}
			String url = "https://graph.microsoft.com/v1.0/me/drive/root"+path+"/children";
			JSONParser parser = new JSONParserOneDrive();

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			DatabaseOp db = new DatabaseOp();
			Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserOneDriveToken(id)).accept(MediaType.APPLICATION_JSON).get();
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		
			String output =  response.readEntity(String.class);
			
			return parser.parserFiles((output));

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return new LinkedList<>();
		
		
	}

	@Override
	public boolean removeFile(String idFile,String path, String idUser) {
		String url = "https://graph.microsoft.com/v1.0/me/drive/items/"+idFile;
		
		try{
			
			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			jerseyClient.register(MultiPartFeature.class);
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);
			
	
			DatabaseOp db = new DatabaseOp();
			
		    Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).delete();
			
			if (response.getStatus() != 204) {//204 == success de la suppression du fichier
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		
			return true;		

		}catch(Exception e){
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean uploadFile(String pathToFile, String pathInDrive,String userId) {
		System.out.println("fdsfd");
		/**
		 * To do :
		 * dire le parent du fichier => avec path
		 * diviser en chunk ( comment definir la taille ?)
		 * resume si echec connexion?
		 */
				try{	
					java.io.File file = new java.io.File(pathToFile);

					String url = "https://graph.microsoft.com/v1.0/me/drive/items/root:/"+file.getName()+":/createUploadSession";

					JerseyClient jerseyClient = JerseyClientBuilder.createClient();
					jerseyClient.register(MultiPartFeature.class);
					jerseyClient.register(new LoggingFilter());
					JerseyWebTarget jerseyTarget = jerseyClient.target(url);
					jerseyClient.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);

					DatabaseOp db = new DatabaseOp();

					/*
					 * Start resumable session
					 */
					String jsonData = "{\n" + 
							"  \"item\": {\n" + 
							"    \"@microsoft.graph.conflictBehavior\": \"rename\",\n" + 
							"    \"name\": \""+file.getName()+"\"\n" + 
							"  }\n" + 
							"}";

					String mimeType = URLConnection.guessContentTypeFromName(file.getName());

					System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

					Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Content-Type","application/json; charset=UTF-8").header("Authorization", "Bearer "+db.getUserOneDriveToken("2"))
							.post(Entity.json(jsonData));


					if (response.getStatus() != 200) {

						throw new RuntimeException("Failed : HTTP error code : "
								+ response.getStatus()+ " "+ response.toString() +  response.readEntity(String.class));
					}

					ObjectMapper mapper = new ObjectMapper();

					String responseJSON = response.readEntity(String.class);
					JsonNode rootNode = mapper.readTree(responseJSON);
					String uploadURL  = rootNode.path("uploadUrl").asText();
					
					/*
					 * Send chunk
					 */

					jerseyClient = JerseyClientBuilder.createClient();
					jerseyClient.register(MultiPartFeature.class);
					jerseyTarget = jerseyClient.target(uploadURL);

					long chunkSize = file.length();//Mieux calculer car upload en un seul chunk useless
					long startRange = 0;
					boolean done = false;

					while(!done) {
						byte[] buffer = new byte[(int) chunkSize];
						FileInputStream fileInputStream = new FileInputStream(file);
						fileInputStream.getChannel().position(startRange);
						fileInputStream.read(buffer, 0, (int) chunkSize);
						fileInputStream.close();

						System.out.println("bytes "+startRange+"-"+(startRange+chunkSize-1)+"/"+file.length());
						response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Content-Length",chunkSize).header("Content-Range", "bytes "+startRange+"-"+(startRange+chunkSize-1)+"/"+file.length())
								.put(Entity.entity(buffer,"application/octet-stream"));

						if (response.getStatus() == 201) {//Success
							done=true;
							System.out.println("File : = "+response.readEntity(String.class));

						}
						else if(response.getStatus() != 202 ||response.getStatus() !=200  ) {
							//resume
							throw new RuntimeException("Failed : HTTP error code : "
									+ response.getStatus()+ " "+ response.toString() +  response.readEntity(String.class));
						}
						else {
							String range = response.getHeaderString("range");
							System.out.println(range);

							//startRange = Long.parseLong(range.substring(range.lastIndexOf("-") + 1, range.length())) + 1;
							//chunkSize = file.length()-startRange;

							
							System.out.println("startrange = "+startRange);
						}


					}



					return true;		

				}catch(Exception e){
					e.printStackTrace();
				}
				return false;
	}

	@Override
	public boolean renameFile(String idFile, String path, String name, String idUser) {
		/*
String url = "https://graph.microsoft.com/v1.0/me/drive/items/"+idFile;
		
		try{
			
			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			jerseyClient.register(MultiPartFeature.class);
			jerseyClient.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);

			JerseyWebTarget jerseyTarget = jerseyClient.target(url);
			
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target("http://localhost:8082/spring-jersey");
	
			webTarget.request().p
			
			DatabaseOp db = new DatabaseOp();
			
			String jsonData = "{\n" + 
					"  \"name\": \""+name+"\"\n" + 
					"}";
			
		    Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).put(Entity.json(jsonData));
			
			if (response.getStatus() != 204) {//204 == success de la suppression du fichier
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString());
			}		
			return true;		

		}catch(Exception e){
			e.printStackTrace();
		}
*/
		return false;
	}

	@Override
	public boolean moveFile(String idFile, String path, String idParent, String pathParent, String idUser) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public String freeSpaceRemaining(String idUser) throws JsonProcessingException, IOException {
		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target("https://graph.microsoft.com/v1.0/me/drive");

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer "+db.getUserOneDriveToken("2"))
				.get();
		
		
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus()+ " "+ response.toString());
		}		
		
		ObjectMapper mapper = new ObjectMapper();

		String responseJSON = response.readEntity(String.class);
		JsonNode rootNode = mapper.readTree(responseJSON);
		JsonNode quotaNode  = rootNode.path("quota");

		
		long quota  = quotaNode.path("total").asLong();
		long free  = quotaNode.path("remaining").asLong();

				
		return "{ \"quota\" : \""+quota+"\",\"used\" : \""+(quota-free)+"\",\"freeSpace\" : \""+free+"\" }";
	}

	

}
