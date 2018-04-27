package com.droovy.request;

import java.util.Date;

public class File {

	private String name, id, url;
	
	private String[] source;
	
	private Date creationDate, lastUpdateDate;

	private int taille;
	
	public File(String name, String id, String url, String[] source, Date creationDate, Date lastUpdateDate,
			int taille) {
		this.name = name;
		this.id = id;
		this.url = url;
		this.source = source;
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
		this.taille = taille;
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

	public int getTaille() {
		return taille;
	}
	
	
}
