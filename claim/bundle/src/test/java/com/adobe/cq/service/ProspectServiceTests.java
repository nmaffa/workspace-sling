package com.adobe.cq.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author nmaffa
 */

//Class should be responsible for testing ProspectService methods. Issue of injecting
//mock repository into service is still unresolved. As such, only one test has been implemented
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
