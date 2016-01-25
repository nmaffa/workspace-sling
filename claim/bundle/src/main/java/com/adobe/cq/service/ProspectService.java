package com.adobe.cq.service;

import java.util.List;

import org.w3c.dom.Document;

public interface ProspectService {
	
	//Mark multiple prospects as approved
	public int approveProspects(List<String> uuids);
	
	//Get all prospect data as a String in XML format
	public String getProspects();

}
