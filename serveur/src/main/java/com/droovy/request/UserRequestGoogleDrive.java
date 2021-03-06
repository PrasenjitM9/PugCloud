package com.droovy.request;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import com.droovy.DatabaseOp;
import com.droovy.JSONParser.JSONParser;
import com.droovy.JSONParser.JSONParserGoogledrive;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.filter.LoggingFilter;

import com.droovy.errors.InternalServerError;
import com.droovy.errors.UserApplicationError;

public class UserRequestGoogleDrive implements UserRequest{

	JSONParser parser = new JSONParserGoogledrive();

	@Override
	public Page getFilesList(String path,String id,boolean folderOnly) {

		path = "q=%27"+path+"%27%20in%20parents";

		String url = "https://www.googleapis.com/drive/v2/files?"+path;

		if(folderOnly) {
			url+="%20and+%20mimeType+%20+%3d+%20%27application%2Fvnd.google-apps.folder%27";
		}

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		DatabaseOp db = new DatabaseOp();
		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(id)).accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 ) {
				throw new UserApplicationError("Set/Update your googledrive token", 401);
			}
			else {
				throw new InternalServerError("Check your file ID");
			}
		}		
		String output =  response.readEntity(String.class);

		List<File> list = new LinkedList<>();

		try {
			list = parser.parserFiles((output));
		} catch (Exception e) {
			throw new InternalServerError();
		}


		try {
			return new Page(list,new ObjectMapper().readTree(output).has("nextPageToken") ? "true" : "false",new ObjectMapper().readTree(output).path("nextPageToken").asText());
		} catch (Exception e) {
			throw new InternalServerError();
		}	


	}



	@Override
	public boolean removeFile(String idFile,String path, String idUser) {
		String url = "https://www.googleapis.com/drive/v2/files/"+idFile;

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.register(MultiPartFeature.class);
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);


		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).delete();

		if (response.getStatus() != 204) {//204 == success de la suppression du fichier
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your google drive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		
		return true;		



	}



	@Override
	public File uploadFile(String pathToFile, String pathInDrive,String userId,String parentId) {

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
				"	\"name\": \""+file.getName()+"\"" + 
				"}";

		String mimeType = URLConnection.guessContentTypeFromName(file.getName());

		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("X-Upload-Content-Length", file.length()).header("X-Upload-Content-Type", mimeType).header("Content-Length",400).header("Content-Type","application/json; charset=UTF-8").header("Authorization", "Bearer "+db.getUserGoogleDriveToken(userId))
				.post(Entity.json(jsonData));


		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your google drive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}


		String uploadURL = response.getHeaderString("location");

		/*
		 * Send chunk
		 */

		jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.register(MultiPartFeature.class);
		jerseyTarget = jerseyClient.target(uploadURL);

		long chunkSize =0;

		if(file.length() < 10*1024*1024) { //Si la taille du fichier inférieur à 10 mo
			chunkSize = file.length();
		}
		else {
			chunkSize = 10*1024*1024;
		}
		long startRange = 0;
		boolean done = false;

		String idUploadedFile="";

		while(!done) {
			byte[] buffer = new byte[(int) chunkSize];

			try {

			}
			catch (Exception e) {
				throw new InternalServerError();
			}

			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(file);
				fileInputStream.getChannel().position(startRange);
				fileInputStream.read(buffer, 0, (int) chunkSize);
				fileInputStream.close();

			}
			catch (Exception e) {
				throw new InternalServerError();
			}


			response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Content-Length",chunkSize).header("Content-Type",mimeType).header("Content-Range", "bytes "+startRange+"-"+(startRange+chunkSize-1)+"/"+file.length())
					.put(Entity.entity(buffer,"application/octet-stream"));

			if (response.getStatus() == 200) {//Success
				done=true;
				String output = response.readEntity(String.class);
				ObjectMapper mapper = new ObjectMapper();

				JsonNode rootNode;
				try {
					rootNode = mapper.readTree(output);
				} catch (Exception e) {
					throw new InternalServerError();
				}
				idUploadedFile =  rootNode.path("id").asText();

			}
			else if(response.getStatus() != 308) {

				throw new InternalServerError();

			}
			else {
				String range = response.getHeaderString("range");

				startRange = Long.parseLong(range.substring(range.lastIndexOf("-") + 1, range.length())) + 1;

				if(file.length() - startRange  < chunkSize) {
					chunkSize = file.length() - startRange;
				}	
			}


		}

		if(idUploadedFile.equals("")) {
			throw new InternalServerError();
		}

		return this.moveFile(idUploadedFile, "", parentId, "", userId, file.getName());

	}


	@Override
	public File renameFile(String idFile, String path, String name, String idUser) {

		String url = "https://www.googleapis.com/drive/v2/files/"+idFile;


		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.register(MultiPartFeature.class);
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String json = "{\"title\":\""+name+"\"}";

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).put(Entity.json(json));

		if (response.getStatus() != 200) {


			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your google drive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		

		try {
			return new JSONParserGoogledrive().parserFile(new ObjectMapper().readTree(response.readEntity(String.class)));
		} catch (Exception e) {
			throw new InternalServerError();
		}

	}



	@Override
	public File moveFile(String idFile, String path, String idParent, String pathParent, String idUser,String name) {

		String url = "https://www.googleapis.com/drive/v2/files/"+idFile;

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.register(MultiPartFeature.class);
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String json = "{\"parents\":[{\"kind\": \"drive#parentReference\",\"id\":\""+idParent+"\"}]}";

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).put(Entity.json(json));

		if (response.getStatus() != 200) {//204 == success rename

			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your google drive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}	
		try {
			return new JSONParserGoogledrive().parserFile(new ObjectMapper().readTree(response.readEntity(String.class)));
		} catch (Exception e) {
			throw new InternalServerError();
		}


	}



	@Override
	public String freeSpaceRemaining(String idUser) throws JsonProcessingException, IOException {

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target("https://www.googleapis.com/drive/v2/about");

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser))
				.get();


		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your google drive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		

		ObjectMapper mapper = new ObjectMapper();

		String responseJSON = response.readEntity(String.class);
		JsonNode rootNode = mapper.readTree(responseJSON);

		long quota  = rootNode.path("quotaBytesTotal").asLong();
		long used  = rootNode.path("quotaBytesUsed").asLong();

		long freeSpace = quota - used ;

		return "{ \"quota\" : \""+quota+"\",\"used\" : \""+used+"\",\"freeSpace\" : \""+freeSpace+"\" }";
	}



	@Override
	public List<File> searchFile(String idUser, String query) {


		String path = "q=name%20contains%20%27"+query+"%27";


		String url = "https://www.googleapis.com/drive/v3/files?"+path;

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		DatabaseOp db = new DatabaseOp();
		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your google drive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		
		String output =  response.readEntity(String.class);
		try {
			return parser.parserFilesSearch(output);
		} catch (Exception e) {
			throw new InternalServerError();
		}

	}

	@Override
	public java.io.File downloadFile(String idUser, String idFile) {


		String url = "https://www.googleapis.com/drive/v3/files/"+idFile+"?alt=media";

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		DatabaseOp db = new DatabaseOp();
		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your google drive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		
		java.io.File output =  response.readEntity(java.io.File.class);
		return output;



	}



	@Override
	public File createFolder(String idUser, String folderName, String path, String idParent) {
		String url = "https://www.googleapis.com/drive/v3/files/";

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.register(MultiPartFeature.class);
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String json = "{\n" + 
				"    \"name\": \""+folderName+"\",\n" + 
				"    \"mimeType\": \"application/vnd.google-apps.folder\",\n" + 
				"    \"parents\": [\""+idParent+"\"]\n" + 
				"}";

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).post(Entity.json(json));

		if (response.getStatus() != 200) {//204 == success rename

			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your google drive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}	
		try {
			return new JSONParserGoogledrive().parserFileSearch(new ObjectMapper().readTree(response.readEntity(String.class)));
		} catch (Exception e) {
			throw new InternalServerError();
		}


	}



	@Override
	public Page nextPage(String idUser, String tokenNextPage,String folderId) {

		String url = "https://www.googleapis.com/drive/v2/files?pageToken="+tokenNextPage;

		url+="&q=%27"+folderId+"%27%20in%20parents";


		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		DatabaseOp db = new DatabaseOp();
		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 ) {
				throw new UserApplicationError("Set/Update your googledrive token", 401);
			}
			else {
				throw new InternalServerError("Check your file ID");
			}
		}		
		String output =  response.readEntity(String.class);

		List<File> list = new LinkedList<>();
		try {
			list = parser.parserFiles((output));
		} catch (Exception e) {
			throw new InternalServerError();
		}


		try {
			return new Page(list,new ObjectMapper().readTree(output).has("nextPageToken") ? "true" : "false",new ObjectMapper().readTree(output).path("nextPageToken").asText());
		} catch (Exception e) {
			throw new InternalServerError();
		}	

	}



	@Override
	public boolean shareFile(String idUser, String message, String idFile, String mail, FilePermission permission,boolean folder) {

		String url = "https://www.googleapis.com/drive/v3/files/"+idFile+"/permissions?sendNotificationEmails=true&emailMessage="+message;

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.register(MultiPartFeature.class);
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String json = "{" + 
				"  \"role\": \""+(permission==FilePermission.READ ? "reader" : "writer")+"\"," + 
				"  \"type\": \"user\"," + 
				"  \"emailAddress\" : \""+mail+"\""+
				"}";

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).post(Entity.json(json));

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your dropbox token,or your token is invalid or you don't have the rights to do this",401);
			}
			else {
				throw new InternalServerError();
			}
		}	
	

		return true;
	}
	
	@Override
	public List<Permission> getFilePermission(String idFile, String idUser) {
		
		
		String url = "https://www.googleapis.com/drive/v2/files/"+idFile+"/permissions";
		
		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		DatabaseOp db = new DatabaseOp();
		
		
		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserGoogleDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 ) {
				throw new UserApplicationError("Set/Update your googledrive token", 401);
			}
			else {
				throw new InternalServerError("Check your file ID");
			}
		}		
		String output =  response.readEntity(String.class);

		List<Permission> listPermission = new LinkedList<>();

		try {
			listPermission = parser.parserPermission(output);
		} catch (Exception e) {
			throw new InternalServerError();
		}
		
		return listPermission;
	}


}
