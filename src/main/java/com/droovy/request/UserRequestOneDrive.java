package com.droovy.request;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.JerseyWebTarget;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.droovy.DatabaseOp;
import com.droovy.JSONParser.JSONParser;
import com.droovy.JSONParser.JSONParserOneDrive;

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
		return false;
	}

	@Override
	public boolean renameFile(String idFile, String path, String name, String idUser) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean moveFile(String idFile, String path, String idParent, String pathParent, String idUser) {
		// TODO Auto-generated method stub
		return false;
	}



	

}
