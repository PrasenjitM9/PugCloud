package com.droovy.JSONParser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.droovy.request.File;
import com.droovy.request.FileType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class JSONParserOneDrive implements JSONParser {

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	@Override
	public List<File> parserFiles(String result) throws JsonProcessingException, IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		List<File> listFile = new ArrayList<File>();

		JsonNode rootNode = mapper.readTree(result);
		JsonNode items = (ArrayNode) rootNode.path("value");

		for (final JsonNode file : items) {
			listFile.add(parserFile(file));
		}
		return listFile;
	}

	@Override
	public File parserFile(JsonNode file) throws JsonProcessingException, IOException, ParseException {

		String id = file.path("id").asText();
		String name = file.path("name").asText();
		String source = "OneDrive";
		String url = file.path("webUrl").asText();
		
		if(file.has("folder")) {
			return new File(name, FileType.FOLDER, id, url,source, null,null, 0, null);
		}
		else {
			
			Date creationDate = formatter.parse(file.path("createdDateTime").asText());
			Date lastUpdateDate = formatter.parse(file.path("lastModifiedDateTime").asText());
			Long size =  file.path("size").asLong();
			String contentHash = file.path("file").path("hashes").path("sha1Hash").asText();
			return new File(name, FileType.FILE, id, url,source,creationDate,lastUpdateDate,size,contentHash);
		}
	
	
	}

	@Override
	public List<File> parserFilesSearch(String output) throws JsonProcessingException, IOException, ParseException {
		ObjectMapper mapper = new ObjectMapper();
		List<File> listFile = new ArrayList<File>();

		JsonNode rootNode = mapper.readTree(output);
		JsonNode items = (ArrayNode) rootNode.path("value");

		for (final JsonNode file : items) {
			listFile.add(parserFile(file));
		}
		return listFile;
	}
	
}
