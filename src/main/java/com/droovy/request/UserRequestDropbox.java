package com.droovy.request;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.droovy.JSONParser.JSONParserDropbox;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.filter.LoggingFilter;

import errors.InternalServerError;
import errors.UserApplicationError;

public class UserRequestDropbox implements UserRequest{
	@Override
	public List<File> getFilesList(String path,String id,boolean folderOnly)  {

		String url = "https://api.dropboxapi.com/2/files/list_folder";
		JSONParser parser = new JSONParserDropbox();

		if(path.equals("root")) {
			path="";
		}

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String jsonData = "{\"path\": \""+path+"\",\"recursive\": false,\"include_media_info\": false,\"include_deleted\": false,\"include_has_explicit_shared_members\": false,\"include_mounted_folders\": true }";

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserDropBoxToken(id)).header("Content-Type", "application/json").accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your dropbox token", 401);
			}
			else {
				throw new InternalServerError("Check your file ID");
			}
		}		

		String output =  response.readEntity(String.class);
		List<File> list = new LinkedList<>();
		try {
			list = parser.parserFiles(output);
		} catch (Exception e) {
			throw new InternalServerError();
		}

		return list;	

	}



	@Override
	public boolean removeFile(String idFile,String path, String idUser) {
		String url = "https://api.dropboxapi.com/2/files/delete_v2";


		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.register(MultiPartFeature.class);
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String jsonData = "{\"path\": \""+path+"\"}";

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Content-Type", "application/json").header("Authorization", "Bearer "+db.getUserDropBoxToken(idUser)).accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));

		if (response.getStatus() != 200) {
			if(response.getStatus() == 401 || response.getStatus()== 400 ) {
				throw new UserApplicationError("Set/Update your dropbox token", 401);
			}
			else if(response.getStatus() == 409) {
				throw new UserApplicationError("File/folder not found", 404);
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

		String url = "https://content.dropboxapi.com/2/files/upload_session/start";

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.register(MultiPartFeature.class);
		jerseyClient.register(new LoggingFilter());
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);
		jerseyClient.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT, LoggingFeature.Verbosity.PAYLOAD_ANY);

		DatabaseOp db = new DatabaseOp();

		/*
		 * Start resumable session
		 */
		String dropboxargs = "{\"close\": false}" ;

		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Dropbox-API-Arg", dropboxargs).header("Content-Type","application/octet-stream").header("Authorization", "Bearer "+db.getUserDropBoxToken("2"))
				.post(Entity.entity(null,MediaType.APPLICATION_OCTET_STREAM));


		if (response.getStatus() != 200) {

			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your dropbox token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}

		ObjectMapper mapper = new ObjectMapper();

		String responseJSON = response.readEntity(String.class);
		JsonNode rootNode;
		try {
			rootNode = mapper.readTree(responseJSON);
		} catch (Exception e) {
			throw new InternalServerError();
		}

		String sessionID  = rootNode.path("session_id").asText();

		String uploadURL = "https://content.dropboxapi.com/2/files/upload_session/append_v2";

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

		long totalUpload = 0;
		boolean done = false;

		while(!done) {
			dropboxargs = "{\"cursor\": {\"session_id\": \""+sessionID+"\",\"offset\": "+totalUpload+"},\"close\": false}";

			byte[] buffer = new byte[(int) chunkSize];

			FileInputStream fileInputStream;
			try {
				fileInputStream = new FileInputStream(file);
				fileInputStream.getChannel().position(totalUpload);
				fileInputStream.read(buffer, 0, (int) chunkSize);
				fileInputStream.close();
			} catch (Exception e) {
				throw new InternalServerError();
			}


			response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Dropbox-API-Arg", dropboxargs).header("Content-Type", "application/octet-stream").header("Authorization", "Bearer "+db.getUserDropBoxToken(userId))
					.post(Entity.entity(buffer,"application/octet-stream"));

			if (response.getStatus() != 200) {

				if(response.getStatus()==401 || response.getStatus() == 400) {
					throw new UserApplicationError("Set/Update your dropbox token,or your token is invalid",401);
				}
				else {
					throw new InternalServerError();
				}

			}	
			if (response.getStatus() == 200) {//Success

				totalUpload += chunkSize;


				if(file.length() - totalUpload < chunkSize) {
					chunkSize = file.length() - totalUpload;
				}

				if(totalUpload == file.length()) {
					done=true;
				}

			}
		}

		dropboxargs="{" + 
				"    \"cursor\": {" + 
				"        \"session_id\": \""+sessionID+"\"," + 
				"        \"offset\": "+file.length() + 
				"    }," + 
				"    \"commit\": {" + 
				"        \"path\": \""+pathInDrive+"\"," + 
				"        \"mode\": \"add\"," + 
				"        \"autorename\": true," + 
				"        \"mute\": false" + 
				"    }" + 
				"}";
		jerseyTarget = jerseyClient.target("https://content.dropboxapi.com/2/files/upload_session/finish");

		response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Dropbox-API-Arg", dropboxargs).header("Content-Type", "application/octet-stream").header("Authorization", "Bearer "+db.getUserDropBoxToken("2"))
				.post(Entity.entity(null,MediaType.APPLICATION_OCTET_STREAM));

		if (response.getStatus() != 200) {
			if(response.getStatus()==409) {
				throw new UserApplicationError("This file already exist",409);
			}
			throw new InternalServerError();
		}

		String output = response.readEntity(String.class);
		try {
			return new JSONParserDropbox().parserFile(new ObjectMapper().readTree(output));
		} catch (Exception e) {
			throw new InternalServerError();
		}
	}


	@Override
	public File renameFile(String idFile, String path, String name, String idUser) {

		String url = "https://api.dropboxapi.com/2/files/move_v2";


		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String jsonData = "{\n" + 
				"    \"from_path\": \""+path+"\",\n" + 
				"    \"to_path\": \""+path.substring(0, path.lastIndexOf('/')+1)+name+"\",\n" + 
				"    \"allow_shared_folder\": false,\n" + 
				"    \"autorename\": false,\n" + 
				"    \"allow_ownership_transfer\": false\n" + 
				"}";

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserDropBoxToken(idUser)).header("Content-Type", "application/json").accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));


		if (response.getStatus() != 200) {
			if(response.getStatus() == 409) {				
				throw new UserApplicationError("Conflict, a file already has this name, or this file doesn't exist",409);
			}
			else if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your dropbox token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}

		}		
		String output = response.readEntity(String.class);

		try {
			return new JSONParserDropbox().parserFile(new ObjectMapper().readTree(output).path("metadata"));
		} catch (Exception e) {
			throw new InternalServerError();
		}


	}


	@Override
	public File moveFile(String idFile, String path, String idParent, String pathParent, String idUser,String name) {

		String url = "https://api.dropboxapi.com/2/files/move_v2";


		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String jsonData = "{\n" + 
				"    \"from_path\": \""+pathParent+"\",\n" + 
				"    \"to_path\": \""+path+"\",\n" + 
				"    \"allow_shared_folder\": false,\n" + 
				"    \"autorename\": false,\n" + 
				"    \"allow_ownership_transfer\": false\n" + 
				"}";
		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserDropBoxToken(idUser)).header("Content-Type", "application/json").accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));

		if (response.getStatus() != 200) {
			
			System.out.println(response.readEntity(String.class));
			
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your dropbox token,or your token is invalid",401);
			}
			else if(response.getStatus() == 409) {				
				throw new UserApplicationError("Conflict, a file already has this name",409);
			}
			else {
				throw new InternalServerError();
			}
		}		
		String output = response.readEntity(String.class);

		try {
			return new JSONParserDropbox().parserFile(new ObjectMapper().readTree(output).path("metadata"));
		} catch (Exception e) {
			throw new InternalServerError();
		}

	}


	@Override
	public String freeSpaceRemaining(String idUser) throws JsonProcessingException, IOException {

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target("https://api.dropboxapi.com/2/users/get_space_usage");

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer "+db.getUserDropBoxToken(idUser))
				.post(null);


		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your dropbox token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		

		ObjectMapper mapper = new ObjectMapper();

		String responseJSON = response.readEntity(String.class);
		JsonNode rootNode = mapper.readTree(responseJSON);
		JsonNode quotaNode  = rootNode.path("allocation");

		long quota  = quotaNode.path("allocated").asLong();
		long used  = rootNode.path("used").asLong();


		long freeSpace = quota - used;

		return "{ \"quota\" : \""+quota+"\",\"used\" : \""+used+"\",\"freeSpace\" : \""+freeSpace+"\" }";

	}

	/*
	@Override
	public boolean shareFile(String idUser, String message, String idFile, String mail,FilePermission permission,boolean folder) {
		String url = "https://api.dropboxapi.com/2/sharing/add_file_member";


		try{

			String filePermission = "editor";
			if(permission == FilePermission.WRITE) {
				filePermission="viewer";
			}

			String jsonData = "";

			if(folder) {
				url = "https://api.dropboxapi.com/2/sharing/add_folder_member";
				jsonData = "{" + 
						"    \"shared_folder_id\": \""+idFile+"\"," + 
						"    \"members\": [ {" + 
						"       \"member\": {" + 
						"                \".tag\": \"email\"," + 
						"                \"email\": \""+mail+"\"" + 
						"            }," + 
						"            \"access_level\": \""+filePermission+"\""+
						"			}"+
						"    ]," + 
						"    \"custom_message\": \""+message+"\"," + 
						"    \"quiet\": false" + 
						"}";

			}
			else {
				jsonData = "{" + 
						"    \"file\": \""+idFile+"\"," + 
						"    \"members\": [" + 
						"        {" + 
						"            \".tag\": \"email\"," + 
						"            \"email\": \""+mail+"\"" + 
						"        }" + 
						"    ]," + 
						"    \"custom_message\": \""+message+"\"," + 
						"    \"quiet\": false," + 
						"    \"access_level\": \""+filePermission+"\"," + 
						"    \"add_message_as_comment\": false" + 
						"}";

			}

			JerseyClient jerseyClient = JerseyClientBuilder.createClient();
			jerseyClient.register(MultiPartFeature.class);
			JerseyWebTarget jerseyTarget = jerseyClient.target(url);



			DatabaseOp db = new DatabaseOp();

			Response response = jerseyTarget.request().header("Content-Type", "application/json").header("Authorization", "Bearer "+db.getUserDropBoxToken(idUser)).accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus()+ " "+ response.toString()+ response.readEntity(String.class));
			}		
			return true;		

		}catch(Exception e){
			e.printStackTrace();
		}

		return false;
	}

	 */

	@Override
	public List<File> searchFile(String idUser, String query) {

		String url = "https://api.dropboxapi.com/2/files/search";
		JSONParser parser = new JSONParserDropbox();


		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String jsonData = "" + 
				"{" +
				"    \"path\": \"\"," + 
				"    \"query\": \""+query+"\"," + 
				"    \"start\": 0," + 
				"    \"max_results\": 100," + 
				"    \"mode\": \"filename\"" + 
				"}";
		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserDropBoxToken(idUser)).header("Content-Type", "application/json").accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your dropbox token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		

		String output =  response.readEntity(String.class);

		List<File> list = new LinkedList<>();
		try {
			list = parser.parserFilesSearch(output);
		} catch (Exception e) {
			throw new InternalServerError();
		}
		return list;
	}


	
	@Override
	public java.io.File downloadFile(String idUser, String idFile) {

		String url = "https://content.dropboxapi.com/2/files/download";
		JSONParser parser = new JSONParserDropbox();


		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		String jsonData = "{" + 
				"    \"path\": \""+idFile+"\"" + 
				"}";
		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Dropbox-API-Arg", jsonData).header("Authorization", "Bearer "+db.getUserDropBoxToken(idUser)).accept(MediaType.APPLICATION_OCTET_STREAM).post(null);

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				String output = response.readEntity(String.class);
				System.out.println(output);
				throw new UserApplicationError("Set/Update your dropbox token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		

		java.io.File output =  response.readEntity(java.io.File.class);
		return output;
	}




}
