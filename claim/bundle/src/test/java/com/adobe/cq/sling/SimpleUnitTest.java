package com.adobe.cq.sling;

import static org.junit.Assert.*;

import org.junit.Test;

import com.adobe.cq.service.ProspectService;
import com.adobe.cq.service.ProspectServiceImpl;

public class SimpleUnitTest {

    @Test
    public void someTest() {
        assertTrue(true);
    }
    
    @Test
    public void prospectServiceFail(){
    	ProspectService prospectService = new ProspectServiceImpl();
    	
    	prospectService.approveProspects("");
    }

}