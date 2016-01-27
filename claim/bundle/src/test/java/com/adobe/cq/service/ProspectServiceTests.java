package com.adobe.cq.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProspectServiceTests {
	
	ProspectService prospectService = new ProspectServiceImpl();
	
    @Test
    public void prospectServiceBadStringTest(){
    	
    	//Arrange
    	String badData = "not a valid path";
    	int expected = -1;
    	
    	//Act
    	int actual = prospectService.approveProspects(badData);
    	
    	//Assert
    	assertEquals(expected, actual);
    	
    }

}
