package com.droovy.request;

import java.io.Serializable;
import java.util.Date;

public class PropertiesFile implements Serializable {
	
	public String id, url, contentHash;
	public Date creationDate, lastUpdateDate;
	
	
	public PropertiesFile(String id, String url, String contentHash, Date creationDate, Date lastUpdateDate) {
		super();
		this.id = id;
		this.url = url;
		this.contentHash = contentHash;
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
	}
	
	

}
