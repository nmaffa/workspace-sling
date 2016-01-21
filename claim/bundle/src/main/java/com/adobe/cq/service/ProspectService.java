package com.adobe.cq.service;

import org.w3c.dom.Document;

public interface ProspectService {
	
	//Mark a prospect, identified by email, as approved
	public int approveProspect(String email);
	
	//Get all prospects
	public String getProspects();

}
