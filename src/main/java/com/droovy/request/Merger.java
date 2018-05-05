package com.droovy.request;

import java.util.List;

public class Merger {
	public List<File> merge(List<File> a, List<File> b, List<File> c) {
		//TO DO 
		a.addAll(b);
		a.addAll(c);
		
		return a;
	}
	
}
