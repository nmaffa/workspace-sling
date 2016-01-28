package com.adobe.cq.service;

/**
 * @author nmaffa
 */

//Service responsible for getting prospect data and approving prospects
public interface ProspectService {
	
	//Mark multiple prospects as approved
	//public int approveProspects(List<String> ids);
	public int approveProspects(String idString);
	
	//Get all prospect data as a String in XML format
	public String getProspects();

}
