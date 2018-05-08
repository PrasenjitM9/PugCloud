package com.droovy.request;

import java.util.List;

public class Merger {
	public List<File> merge(List<File> listGoogleDrive, List<File> listDropbox, List<File> listOneDrive) {
		 	
		//Nom du fichier
		for (int i=0;i<listGoogleDrive.size();i++) {
			
			File fileGoogleDrive = listGoogleDrive.get(i);
			
			for (int j=0;j<listDropbox.size();j++) {
				
				File fileDropbox = listDropbox.get(j);
				
				if(fileDropbox.getName().equals(fileGoogleDrive.getName()) && fileDropbox.getTaille() == fileGoogleDrive.getTaille()){
					fileGoogleDrive.addSource(fileDropbox.getSourceProperties());
					listDropbox.remove(j);
				}
				
				for (int k=0;k<listOneDrive.size();k++) {
					
					File fileOneDrive = listOneDrive.get(k);
					
					if(fileOneDrive.getName().equals(fileGoogleDrive.getName()) && fileOneDrive.getTaille() == fileGoogleDrive.getTaille()){
						fileGoogleDrive.addSource(fileOneDrive.getSourceProperties());
						listOneDrive.remove(k);
					}
					if(fileDropbox.getName().equals(fileOneDrive.getName()) && fileDropbox.getTaille() == fileOneDrive.getTaille()){
						fileDropbox.addSource(fileOneDrive.getSourceProperties());
						listOneDrive.remove(k);
					}
				}
			}
		}
		
		
		listGoogleDrive.addAll(listDropbox);
		listGoogleDrive.addAll(listOneDrive);
		
		return listGoogleDrive;
			
	}
	
	
}
