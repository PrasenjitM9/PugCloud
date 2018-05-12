package com.droovy.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonProcessingException;

import errors.ApplicationException;
import errors.UserFaultException;

public interface UserRequest {
	
	
	public List<File> getFilesList(String path,String id) throws ApplicationException, UserFaultException;
		
	public boolean removeFile(String idFile,String path,String idUser);
	
	public boolean uploadFile(String pathToFile, String pathInDrive,String userId);
	
	public boolean moveFile(String idFile,String path, String idParent, String pathParent, String idUser);
	
	public boolean renameFile(String idFile,String path, String name, String idUser);
	
	public String freeSpaceRemaining(String idUser) throws JsonProcessingException, IOException;
	 
}
