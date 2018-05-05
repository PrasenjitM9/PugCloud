package com.droovy.request;

import java.util.List;

public interface UserRequest {
	
	
	public List<File> getFilesList(String path,String id);
	
	public String addFile(String filename, String id);
	
	public boolean removeFile(String id_file, String id);
	
	
	 
}
