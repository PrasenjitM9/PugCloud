package com.droovy.request;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

public interface UserRequest {
	
	
	public List<File> getFilesList(String path,String id);
		
	public boolean removeFile(String idFile,String path,String idUser);
	
	public boolean uploadFile(String pathToFile, String pathInDrive,String userId);
	
	public boolean moveFile(String idFile,String path, String idParent, String pathParent, String idUser);
	
	public boolean renameFile(String idFile,String path, String name, String idUser);
	 
}
