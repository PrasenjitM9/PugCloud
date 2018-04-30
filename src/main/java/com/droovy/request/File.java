package com.droovy.request;

import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class File {

	private String name, id, url;
	
	private String[] source;
	
	private Date creationDate, lastUpdateDate;

	private long taille;
	
	private FileType type; 
	
	private String contentHash;
	
	public File(String name,FileType type, String id, String url, String[] source, Date creationDate, Date lastUpdateDate,
			long taille, String contentHash) {
		this.name = name;
		this.type=type;
		this.id = id;
		this.url = url;
		this.source = source;
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
		this.taille = taille;
		this.contentHash = contentHash;
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

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public String[] getSource() {
		return source;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public long getTaille() {
		return taille;
	}
	
	public String json() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
}
