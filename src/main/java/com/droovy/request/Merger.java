package com.droovy.request;

import java.util.List;

public class Merger {
	public List<File> merge(List<File> a, List<File> b) {
		//TO DO 
		a.addAll(b);
		
		return a;
	}
	
}
