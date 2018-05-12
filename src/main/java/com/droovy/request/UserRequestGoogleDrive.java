package com.droovy.request;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.grizzly.http.server.util.Mapper;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyInvocation;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.MultiPartMediaTypes;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;

import com.droovy.DatabaseOp;
import com.droovy.JSONParser.JSONParser;
import com.droovy.JSONParser.JSONParserGoogledrive;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;

public class UserRequestGoogleDrive implements UserRequest{

	JSONParser parser = new JSONParserGoogledrive();

	@Override
	public List<File> getFilesList(String path,String id) {

		try{

			path = "q=%27"+path+"%27%20in%20parents";


			String url = "https://www.googleapis.com/drive/v2/files?"+path;

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			DatabaseOp db = new DatabaseOp();
			Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(id)).accept(MediaType.APPLICATION_JSON).get();

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
		String url = "https://www.googleapis.com/drive/v2/files/"+idFile;

		try{

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			jerseyClient.register(MultiPartFeature.class);
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);


			DatabaseOp db = new DatabaseOp();

			Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).delete();

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
 * dire le parent du ficheir
 * diviser en chunk ( comment definir la taille ?)
 * resume si echec connexion?
 */
		try{	
			java.io.File file = new java.io.File(pathToFile);

			String url = "https://www.googleapis.com/upload/drive/v3/files/?uploadType=resumable";

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
					"	\"name\": \"file.getName()\",\n" + 
					"	\"parents\": [{\n" + 
					"		\"id\": \"1yyiiS_h-b6z2uOSrzd0RMlLFeZl_Wm2U\"\n" + 
					"	}]\n" + 
					"}";

			String mimeType = URLConnection.guessContentTypeFromName(file.getName());

			System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

			Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("X-Upload-Content-Length", file.length()).header("X-Upload-Content-Type", mimeType).header("Content-Length",400).header("Content-Type","application/json; charset=UTF-8").header("Authorization", "Bearer "+db.getUserGoogleDriveToken("2"))
					.post(Entity.json(jsonData));


			if (response.getStatus() != 200) {

				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString() +  response.readEntity(String.class));
			}


			String uploadURL = response.getHeaderString("location");

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
				response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Content-Length",chunkSize).header("Content-Type",mimeType).header("Content-Range", "bytes "+startRange+"-"+(startRange+chunkSize-1)+"/"+file.length())
						.put(Entity.entity(buffer,"application/octet-stream"));

				if (response.getStatus() == 200) {//Success
					done=true;
					System.out.println("File : = "+response.readEntity(String.class));

				}
				else if(response.getStatus() != 308) {
					//resume
					throw new RuntimeException("Failed : HTTP error code : "
							+ response.getStatus()+ " "+ response.toString() +  response.readEntity(String.class));
				}
				else {
					String range = response.getHeaderString("range");
					System.out.println(range);

					startRange = Long.parseLong(range.substring(range.lastIndexOf("-") + 1, range.length())) + 1;
					chunkSize = file.length()-startRange;

					
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
		String url = "https://www.googleapis.com/drive/v2/files/"+idFile;

		try{

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			jerseyClient.register(MultiPartFeature.class);
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			String json = "{\"title\":\""+name+"\"}";

			DatabaseOp db = new DatabaseOp();

			Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).put(Entity.json(json));

			if (response.getStatus() != 200) {//204 == success rename

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
	public boolean moveFile(String idFile, String path, String idParent, String pathParent, String idUser,String name) {
		String url = "https://www.googleapis.com/drive/v2/files/"+idFile;

		try{

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			jerseyClient.register(MultiPartFeature.class);
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);

			String json = "{\"parents\":[{\"kind\": \"drive#parentReference\",\"id\":\""+idParent+"\"}]}";

			DatabaseOp db = new DatabaseOp();

			Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).put(Entity.json(json));

			if (response.getStatus() != 200) {//204 == success rename

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
	public String freeSpaceRemaining(String idUser) throws JsonProcessingException, IOException {

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target("https://www.googleapis.com/drive/v2/about");

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer "+db.getUserGoogleDriveToken("2"))
				.get();
		
		
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus()+ " "+ response.toString());
		}		
		
		ObjectMapper mapper = new ObjectMapper();

		String responseJSON = response.readEntity(String.class);
		JsonNode rootNode = mapper.readTree(responseJSON);
		
		long quota  = rootNode.path("quotaBytesTotal").asLong();
		long used  = rootNode.path("quotaBytesUsed").asLong();

		
		System.out.println(used+" "+quota+" ");
		long freeSpace = quota - used ;
		
		return "{ \"quota\" : \""+quota+"\",\"used\" : \""+used+"\",\"freeSpace\" : \""+freeSpace+"\" }";
	}
	
}
