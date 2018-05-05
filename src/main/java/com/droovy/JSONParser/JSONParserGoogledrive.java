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
		ObjectMapper mapper = new ObjectMapper();

		String name = file.path("title").asText();
			
		
		String id = file.path("id").asText();
		String url = file.path("webContentLink").asText();
		String source[] = {"GoogleDrive"};
		Date creationDate = null;
		Date lastUpdateDate = null;
		try {
			creationDate = formatter.parse(file.path("createdDate").asText());
			lastUpdateDate = formatter.parse(file.path("modifiedDate").asText());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long taille = file.path("fileSize").asLong();

		FileType type = FileType.FILE;
		
		if(file.path("mimeType").asText().equals("application/vnd.google-apps.folder")) {
			type = FileType.FOLDER;
		}
		
		return new File(name,type, id, url, source, creationDate, lastUpdateDate, taille,"");

	}

}