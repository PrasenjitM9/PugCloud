package com.droovy.request;

public interface UserRequest {
	
	
	public String getFilesList(String path,String id);
	
	public String addFile(String filename, String id);
	
	public boolean removeFile(String id_file, String id);
	
	
	
}
