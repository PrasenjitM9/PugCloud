package com.droovy.request;

import java.util.List;

public class Merger {
	public List<File> merge(List<File> listGoogleDrive, List<File> listDropbox, List<File> listOneDrive) {
		 	
		
		List<File> list1, list2, list3;
		
		if(listGoogleDrive.size()>listDropbox.size()){
			if(listOneDrive.size()>listGoogleDrive.size()){
				list1 = listOneDrive;
				list2 = listGoogleDrive;
				list3 = listDropbox;
			}
			else {
				if(listOneDrive.size() > listDropbox.size()){
					list1 = listGoogleDrive;
					list2 = listOneDrive;
					list3 = listDropbox;
				}
				else{
					list1 = listGoogleDrive;
					list2 = listDropbox;
					list3 = listOneDrive;
				}
			}
		}else{
			if(listOneDrive.size()>listDropbox.size()){
				list1 = listOneDrive;
				list2 = listDropbox;
				list3 = listGoogleDrive;
			}
			else {
				if(listOneDrive.size() > listGoogleDrive.size()){
					list1 = listDropbox;
					list2 = listOneDrive;
					list3 = listGoogleDrive;
				}
				else{
					list1 = listDropbox;
					list2 = listGoogleDrive;
					list3 = listOneDrive;
				}
			}
		}
		
		
		//Nom du fichier
		for (int i=0;i<list1.size();i++) {
			
			File file1 = list1.get(i);
			
			for (int j=0;j<list2.size();j++) {
				
				File file2 = list2.get(j);
				
				if(file2.getName().equals(file1.getName()) && file2.getTaille() == file1.getTaille()){
					file1.addSource(file2.getSourceProperties());
					list2.remove(j);
				}
				
				for (int k=0;k<list3.size();k++) {
					
					File file3 = list3.get(k);
					
					if(file3.getName().equals(file1.getName()) && file3.getTaille() == file1.getTaille()){
						file1.addSource(file3.getSourceProperties());
						list3.remove(k);
					}
					if(file2.getName().equals(file3.getName()) && file2.getTaille() == file3.getTaille()){
						file2.addSource(file3.getSourceProperties());
						list3.remove(k);
					}
				}
			}
		}
		
		
		list1.addAll(list2);
		list1.addAll(list3);
		
		return list1;
			
	}
	
	
}
