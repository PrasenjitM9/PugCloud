package com.droovy.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserRequest {
	
	
	public Page getFilesList(String path,String id,boolean folderOnly);
		
	public boolean removeFile(String idFile,String path,String idUser);
	
	public File uploadFile(String pathToFile, String pathInDrive,String userId,String parentId);
	
	public File moveFile(String idFile,String path, String idParent, String pathParent, String idUser,String name);
	
	public File renameFile(String idFile,String path, String name, String idUser);
	
	public String freeSpaceRemaining(String idUser) throws JsonProcessingException, IOException;

	public boolean shareFile(String idUser, String message, String idFile, String mail,FilePermission permission,boolean folder);
	
	public List<File> searchFile(String idUser,String query);
	
	public java.io.File downloadFile(String idUser,String idFile);
	
	public File createFolder(String idUser,String folderName,String path, String idParent);
	
	public Page nextPage(String idUser,String tokenNextPage,String folderId);
	
	public List<Permission> getFilePermission(String idFile, String idUser);
	
}
