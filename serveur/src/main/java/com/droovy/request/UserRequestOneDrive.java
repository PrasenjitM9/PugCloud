package com.droovy.request;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Entity;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.filter.LoggingFilter;

import com.droovy.errors.InternalServerError;
import com.droovy.errors.UserApplicationError;

public class UserRequestOneDrive implements UserRequest {

	@Override
	public Page getFilesList(String path,String id,boolean folderOnly) {

		if(!path.equals("root")) {
			path=":"+path+":";
		}
		else {
			path="";
		}
		String url = "https://graph.microsoft.com/v1.0/me/drive/root"+path+"/children";

		if(folderOnly) {
			url+="?filter=folder%20ne%20null";
		}
		JSONParser parser = new JSONParserOneDrive();

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		DatabaseOp db = new DatabaseOp();
		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserOneDriveToken(id)).accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
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
			return new Page(list,new ObjectMapper().readTree(output).has("@odata.nextLink") ? "true" : "false",new ObjectMapper().readTree(output).path("@odata.nextLink").asText());
		} catch (Exception e) {
			throw new InternalServerError();
		}	
	}

	@Override
	public boolean removeFile(String idFile,String path, String idUser) {
		String url = "https://graph.microsoft.com/v1.0/me/drive/items/"+idFile;

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.register(MultiPartFeature.class);
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);


		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).delete();

		if (response.getStatus() != 204) {//204 == success de la suppression du fichier
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
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

		String url = "https://graph.microsoft.com/v1.0/me/drive/items/root:"+pathInDrive+":/createUploadSession";

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

		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Content-Type","application/json; charset=UTF-8").header("Authorization", "Bearer "+db.getUserOneDriveToken(userId))
				.post(Entity.json(jsonData));


		if (response.getStatus() != 200) {

			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus()+ " "+ response.toString() +  response.readEntity(String.class));
		}

		ObjectMapper mapper = new ObjectMapper();

		String responseJSON = response.readEntity(String.class);
		JsonNode rootNode;
		try {
			rootNode = mapper.readTree(responseJSON);
		} catch (Exception e) {
			throw new InternalServerError();
		}
		String uploadURL  = rootNode.path("uploadUrl").asText();

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

		String output ="";

		while(!done) {
			byte[] buffer = new byte[(int) chunkSize];

			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				fileInputStream.getChannel().position(startRange);
				fileInputStream.read(buffer, 0, (int) chunkSize);
				fileInputStream.close();
			}
			catch(Exception e) {
				throw new InternalServerError();
			}



			response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Content-Length",chunkSize).header("Content-Range", "bytes "+startRange+"-"+(startRange+chunkSize-1)+"/"+file.length())
					.put(Entity.entity(buffer,"application/octet-stream"));

			if (response.getStatus() == 201) {//Success
				output=response.readEntity(String.class);
				done=true;
			}
			else if(response.getStatus() != 202 && response.getStatus() !=200  ) {
				if(response.getStatus()==401 || response.getStatus() == 400) {
					throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
				}
				else {
					throw new InternalServerError();
				}
			}
			else {

				startRange += chunkSize;


				if(file.length() - startRange  < chunkSize) {
					chunkSize = file.length() - startRange;
				}

			}


		}
		try {
			return new JSONParserOneDrive().parserFile(new ObjectMapper().readTree(output));
		} catch (Exception e) {
			throw new InternalServerError();
		}

	}

	@Override
	public File renameFile(String idFile, String path, String name, String idUser) {

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);


		JerseyWebTarget jerseyTarget = jerseyClient.target("https://graph.microsoft.com/v1.0/me/drive/items/"+idFile);
		DatabaseOp db = new DatabaseOp();

		String jsonData = "{\n" + 
				"  \"name\": \""+name+"\"\n" + 
				"}";

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).method("PATCH", Entity.json(jsonData));;

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		
		String output = response.readEntity(String.class);
		try {
			return new JSONParserOneDrive().parserFile(new ObjectMapper().readTree(output));
		} catch (Exception e) {
			throw new InternalServerError();
		}


	}

	@Override
	public File moveFile(String idFile, String path, String idParent, String pathParent, String idUser,String name) {

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);


		JerseyWebTarget jerseyTarget = jerseyClient.target("https://graph.microsoft.com/v1.0/me/drive/items/"+idFile);
		DatabaseOp db = new DatabaseOp();


		String jsonData = "{" + 
				" \"parentReference\": {" + 
				" \"id\": \""+idParent+"\"" + 
				" }," + 
				" \"name\": \""+name+"\"" + 
				"}";

		Response response = jerseyTarget.request().header("Content-type", "application/json").header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).method("PATCH", Entity.json(jsonData));;

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}
		}		

		String output = response.readEntity(String.class);
		try {
			return new JSONParserOneDrive().parserFile(new ObjectMapper().readTree(output));
		} catch (Exception e) {
			throw new InternalServerError();
		}

	}



	@Override
	public String freeSpaceRemaining(String idUser)  {
		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target("https://graph.microsoft.com/v1.0/me/drive");

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser))
				.get();


		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
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
		JsonNode quotaNode  = rootNode.path("quota");


		long quota  = quotaNode.path("total").asLong();
		long free  = quotaNode.path("remaining").asLong();


		return "{ \"quota\" : \""+quota+"\",\"used\" : \""+(quota-free)+"\",\"freeSpace\" : \""+free+"\" }";
	}

	/*
	@Override
	public boolean shareFile(String idUser, String message, String idFile, String mail, FilePermission permission,boolean folder) {
		// TODO Auto-generated method stub
		return false;
	}*/

	@Override
	public List<File> searchFile(String idUser, String query) {

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target("https://graph.microsoft.com/v1.0/me/drive/root/search(q='"+query+"')");

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser))
				.get();


		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}	
		}		

		JSONParserOneDrive parser = new JSONParserOneDrive();
		String output = response.readEntity(String.class);
		try {
			return parser.parserFilesSearch(output);
		} catch (Exception e) {
			throw new InternalServerError();
		}

	}

	@Override
	public java.io.File downloadFile(String idUser, String idFile) {
		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target("https://graph.microsoft.com/v1.0/me/drive/items/"+idFile+"/content");

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser))
				.get();


		if (response.getStatus() != 200) {
			String output =  response.readEntity(String.class);
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
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
		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);


		if(path.equals("root")) {
			path = "";
		}
		else {
			path=":"+path+":";
		}

		String url = "https://graph.microsoft.com/v1.0/me/drive/items/root"+path+"/children";

		JerseyWebTarget jerseyTarget = jerseyClient.target(url);
		DatabaseOp db = new DatabaseOp();

		String jsonData = "{" + 
				"  \"name\": \""+folderName+"\"," + 
				"  \"folder\": { }," + 
				"  \"@microsoft.graph.conflictBehavior\": \"rename\"" + 
				"}";

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));;

		if (response.getStatus() != 200 && response.getStatus() != 201) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
			}	
		}		
		String output = response.readEntity(String.class);
		try {
			return new JSONParserOneDrive().parserFile(new ObjectMapper().readTree(output));
		} catch (Exception e) {
			throw new InternalServerError();
		}

	}

	@Override
	public Page nextPage(String idUser, String tokenNextPage,String folderId) {


		String url = tokenNextPage;


		JSONParser parser = new JSONParserOneDrive();

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		DatabaseOp db = new DatabaseOp();
		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {
			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
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
			return new Page(list,new ObjectMapper().readTree(output).has("@odata.nextLink") ? "true" : "false",new ObjectMapper().readTree(output).path("@odata.nextLink").asText());
		} catch (Exception e) {
			throw new InternalServerError();
		}	

	}

	@Override
	public boolean shareFile(String idUser, String message, String idFile, String mail, FilePermission permission,
			boolean folder) {

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		jerseyClient.property(HttpUrlConnectorProvider.SET_METHOD_WORKAROUND, true);

		String url = "https://graph.microsoft.com/v1.0/me/drive/items/"+idFile+"/invite";

		JerseyWebTarget jerseyTarget = jerseyClient.target(url);
		DatabaseOp db = new DatabaseOp();

		String jsonData = "{" + 
				"  \"recipients\": [" + 
				"    {" + 
				"      \"email\": \""+mail+"\"" + 
				"    }" + 
				"  ]," + 
				"  \"message\": \""+message+"\"," + 
				"  \"requireSignIn\": true," + 
				"  \"sendInvitation\": true," + 
				"  \"roles\": [ \""+permission+"\" ]" + 
				"}";

		Response response = jerseyTarget.request().header("Content-type","application/json").header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).post(Entity.json(jsonData));;

		if (response.getStatus() != 200 && response.getStatus() != 201) {
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
		String url = "https://graph.microsoft.com/v1.0/me/drive/items/"+idFile+"/permissions";
				
		JSONParser parser = new JSONParserOneDrive();

		JerseyClient jerseyClient = JerseyClientBuilder.createClient();
		JerseyWebTarget jerseyTarget = jerseyClient.target(url);

		DatabaseOp db = new DatabaseOp();

		Response response = jerseyTarget.request().header("Authorization", "Bearer "+db.getUserOneDriveToken(idUser)).accept(MediaType.APPLICATION_JSON).get();

		if (response.getStatus() != 200) {

			if(response.getStatus()==401 || response.getStatus() == 400) {
				throw new UserApplicationError("Set/Update your onedrive token,or your token is invalid",401);
			}
			else {
				throw new InternalServerError();
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
