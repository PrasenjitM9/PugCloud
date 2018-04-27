package com.droovy.JSONParser;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;

import com.droovy.request.File;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;


public interface JSONParser {

	public List parserFiles(String result) throws JsonProcessingException, IOException, ParseException;
	public File parserFile(JsonNode file) throws JsonProcessingException, IOException, ParseException;
}
