package com.adobe.cq.service;

import org.w3c.dom.Document;

public interface ProspectService {
	
	//Mark a prospect as approved 
	public int approveProspect(String email);
	
	//Get all prospect data as a String in XML format
	public String getProspects();

}
