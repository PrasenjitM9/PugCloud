package com.droovy.request;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class File {

	private String name;
		
	private long taille;
	
	private FileType type; 
	
	private Map<String, PropertiesFile> sourceProperties = new HashMap<>();
	/*
	private String path;
	private sharedMembers
	private String permission;
	*/
	public File(String name,  FileType type,  String id, String url,String source, Date creationDate, 
			Date lastUpdateDate, long taille,String contentHash){
		
		this.name = name;
		this.taille = taille;
		this.type = type;
		
		sourceProperties.put(source, new PropertiesFile(id, url, contentHash, creationDate, lastUpdateDate));
		
	}

	public File() {
	}

	public FileType getType() {
		return type;
	}
	
	public void setType(FileType type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public long getTaille() {
		return taille;
	}
	
	public Map<String, PropertiesFile> getSourceProperties() {
		return sourceProperties;
	}
	
	public void addSource(Map<String, PropertiesFile> sourceProperties) {
		this.sourceProperties.putAll(sourceProperties);
	}
	
	/*
	public String json() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	*/
}
