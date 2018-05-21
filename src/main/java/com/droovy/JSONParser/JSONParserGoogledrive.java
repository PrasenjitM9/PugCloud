package com.droovy.JSONParser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.droovy.request.File;
import com.droovy.request.FileType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class JSONParserGoogledrive implements JSONParser {

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	@Override
	public List<File> parserFiles(String result) throws JsonProcessingException, IOException {
		System.out.println(result);

		ObjectMapper mapper = new ObjectMapper();
		List<File> listFile = new ArrayList<File>();

		JsonNode rootNode = mapper.readTree(result);
		JsonNode items = (ArrayNode) rootNode.path("items");

		for (final JsonNode file : items) {
			listFile.add(parserFile(file));
		}
		return listFile;
	}

	@Override
	public File parserFile(JsonNode file) throws JsonProcessingException, IOException {

		
		String name = file.path("title").asText();
			
		
		String id = file.path("id").asText();
		String url = file.path("alternateLink").asText();//download : webContentLink
		String source = "GoogleDrive";
		Date creationDate = null;
		Date lastUpdateDate = null;
		try {
			creationDate = formatter.parse(file.path("createdDate").asText());
			lastUpdateDate = formatter.parse(file.path("modifiedDate").asText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long size = file.path("fileSize").asLong();
		
		if(file.path("mimeType").asText().equals("application/vnd.google-apps.folder")) {
			return new File(name, FileType.FOLDER, id, url,source, null,null, 0, null);
		}
		return new File(name, FileType.FILE, id, url,source,creationDate,lastUpdateDate,size,"TO DO");

	}

	@Override
	public List<File> parserFilesSearch(String output) throws JsonProcessingException, IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		List<File> listFile = new ArrayList<File>();

		JsonNode rootNode = mapper.readTree(output);
		JsonNode items = (ArrayNode) rootNode.path("files");

		for (final JsonNode file : items) {
			listFile.add(parserFileSearch(file));
		}
		return listFile;
	}

	public File parserFileSearch(JsonNode file) {

		String name = file.path("name").asText();
			
		
		String id = file.path("id").asText();
		String source = "GoogleDrive";

		
		if(file.path("mimeType").asText().equals("application/vnd.google-apps.folder")) {
			return new File(name, FileType.FOLDER, id, "",source, null,null, 0, null);
		}
		return new File(name, FileType.FILE, id, "",source,null,null,0,"TO DO");
	}
	
}
