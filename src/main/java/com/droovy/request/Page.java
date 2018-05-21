package com.droovy.request;

import java.util.LinkedList;
import java.util.List;

public class Page {

	private List<File> listFile ;
	private String hasMore;
	private String nextPageToken;
	
	
	public Page() {
		listFile = new LinkedList<>();
		hasMore ="false";
		nextPageToken = "";
	}
	
	public Page(List<File> listFile, String hasMore, String nextPageToken) {
		super();
		this.listFile = listFile;
		this.hasMore = hasMore;
		this.nextPageToken = nextPageToken;
	}
	public List<File> getListFile() {
		return listFile;
	}
	public String isHasMore() {
		return hasMore;
	}
	public String getNextPageToken() {
		return nextPageToken;
	}
	
	
	
}
