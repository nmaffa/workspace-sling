package com.adobe.cq.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
  
import javax.jcr.Repository; 
import javax.jcr.SimpleCredentials; 
import javax.jcr.Node; 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
   
import org.apache.jackrabbit.commons.JcrUtils;
  
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
  
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import javax.jcr.RepositoryException;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.commons.JcrUtils;
  
import javax.jcr.Session;
import javax.jcr.Node; 
 
 
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory; 
import org.apache.sling.api.resource.ResourceResolver; 
//import org.apache.sling.api.resource.Resource; 

import com.adobe.cq.model.Prospect;
  
  
//This is a component so it can provide or consume services
@Component
  
@Service
public class ProspectServiceImpl implements ProspectService {
  
	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	      
	private Session session;
	          
	//Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;
 
	//Queries the AEM JCR for customer data and returns
	//the data within an XML schema   
	public String getProspects() {
	 
		Prospect prospect = null;
		 
		List<Prospect> prospectList = new ArrayList<Prospect>();
		
		String error = "baseError";
		
		try {
		           
		    //Invoke the adaptTo method to create a Session used to create a QueryManager
			
			//ResourceResolver resourceResolver = resolverFactory.getResourceResolver(null);
			ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
		    session = resourceResolver.adaptTo(Session.class);
		 
		    //Obtain the query manager for the session ...
		    javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();
		      
			//Setup the query based on user input     
			String sqlStatement="";
		     
			//Setup the query to get all prospect records (NEED TO VALIDATE THIS)
			sqlStatement = "SELECT * FROM [nt:unstructured] WHERE ISDESCENDANTNODE([/var/prospects])";
			//sqlStatement = "SELECT * FROM [nt:unstructured] WHERE email='zyz@sapient.com'";
			//sqlStatement = "SELECT * FROM [nt:unstructured] WHERE PATH([nt:unstructured]) LIKE '/var/prospects/%' ";
			sqlStatement += " AND [nt:unstructured].isBlacklist IS NOT NULL AND [nt:unstructured].isBlacklist='true' ";
			sqlStatement += " AND [nt:unstructured].regStatusEmailType IS NOT NULL AND [nt:unstructured].regStatusEmailType='EXPIRED' ";
			sqlStatement += " AND [nt:unstructured].isApproved IS NOT NULL AND [nt:unstructured].isApproved='-1'";
			
			
			//Simple query used for debugging - no relevance to web page
			//sqlStatement = "SELECT * FROM [nt:resource] WHERE [jcr:encoding]='utf-8'";

			//Query used in tutorial
			//sqlStatement = "SELECT * FROM [nt:unstructured] WHERE CONTAINS(desc, 'Customer')";
			         
			javax.jcr.query.Query query = queryManager.createQuery(sqlStatement,"JCR-SQL2");
			 
			//Execute the query and get the results ...
			javax.jcr.query.QueryResult result = query.execute();
			
			//Used in conjunction with above simple query
			//return Long.toString(result.getNodes().getSize());
			 
			//Iterate over the nodes in the results ...
			javax.jcr.NodeIterator nodeIter = result.getNodes();
	 
			while ( nodeIter.hasNext() ) {
			 
				 //For each node-- create a customer instance
				prospect = new Prospect();
			         
				javax.jcr.Node node = nodeIter.nextNode();
			           
				 //Set all Customer object fields
				 prospect.setEmail(node.getProperty("email").getString());
				 prospect.setFname(node.getProperty("fname").getString());
				 prospect.setLname(node.getProperty("lname").getString());
				 prospect.setIsApproved(node.getProperty("isApproved").getString());
			//	 prospect.setCustFirst(node.getProperty("firstName").getString());
			//	 prospect.setCustLast(node.getProperty("lastName").getString());
			//	 prospect.setCustAddress(node.getProperty("address").getString());
			//	 prospect.setCustDescription(node.getProperty("desc").getString());
				           
				  //Push prospect to the list
				  prospectList.add(prospect);
			}
			         
			// Log out
			session.logout();    
			return convertToString(toXml(prospectList));               
			             
		}
		catch(Exception e) {
			e.printStackTrace();
			log.error("Error occured with getProspects()", e);
			error = e.getMessage();
		}
		
		return error;
	}	      
	      
	//Convert Customer data retrieved from the AEM JCR
	//into an XML schema to pass back to client
	private Document toXml(List<Prospect> prospectList) {
	try
	{
	    DocumentBuilderFactory factory =     DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document doc = builder.newDocument();
	                  
	    //Start building the XML to pass back to the AEM client
	    Element root = doc.createElement( "prospects" );
	    doc.appendChild( root );
	               
	    //Get the elements from the collection
	    int prospectCount = prospectList.size();
	     
	    //Iterate through the collection to build up the DOM           
	     for ( int index=0; index < prospectCount; index++) {
	  
	         //Get the Prospect object from the collection
	         Prospect prospect = (Prospect)prospectList.get(index);
	                       
	         Element prospectElement = doc.createElement( "prospect" );
	         root.appendChild( prospectElement );
	                        
	         //Add rest of data as child elements to customer
	         //Set Email
	         Element emailElement = doc.createElement( "email" );
	         emailElement.appendChild( doc.createTextNode( prospect.getEmail() ) );
	         prospectElement.appendChild( emailElement );
	                                                           
	         //Set First Name
	         Element fNameElement = doc.createElement( "fname" );
	         fNameElement.appendChild( doc.createTextNode( prospect.getFname() ) );
	         prospectElement.appendChild( fNameElement );
	                     
	         //Set Last name
	         Element lNameElement = doc.createElement( "lname" );
	         lNameElement.appendChild( doc.createTextNode( prospect.getLname() ) );
	         prospectElement.appendChild( lNameElement );
	                    
	         //Set is Approved
	         Element isApprovedElement = doc.createElement( "isApproved" );
	         isApprovedElement.appendChild( doc.createTextNode( prospect.getIsApproved() ) );
	         prospectElement.appendChild( isApprovedElement );
	      }
	             
	return doc;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    }
	return null;
	}
	  
	  
	private String convertToString(Document xml)
	{
		if (xml == null){return "XML is null";}
	try {
	   Transformer transformer = TransformerFactory.newInstance().newTransformer();
	  StreamResult result = new StreamResult(new StringWriter());
	  DOMSource source = new DOMSource(xml);
	  transformer.transform(source, result);
	  return result.getWriter().toString();
	} catch(Exception ex) {
	      ex.printStackTrace();
	}
	  return "Failure in converting XML to String";
	 }
	
	 
	 
	 
	//Stores prospect data in the Adobe CQ JCR
	public int approveProspect(String email)
	{      
		/*
	  
		int num  = 0;
		try {
	              
		    //Invoke the adaptTo method to create a Session used to create a QueryManager
		    ResourceResolver resourceResolver = resolverFactory.getResourceResolver(null);
		    session = resourceResolver.adaptTo(Session.class);
	               
		    //Create a node that represents the root node
		    Node root = session.getRootNode();
			                     
			//Get the content node in the JCR
			Node content = root.getNode("var/prospects");
			                      
			//Determine if the content/customer node exists
			Node prospectRoot = null;
			int custRec = doesCustExist(content);
			                                            
			//-1 means that content/customer does not exist
			if (custRec == -1){
			     //content/customer does not exist -- create it
				 prospectRoot = content.addNode("customer","nt:unstructured");
			}
			else {
				 //content/customer does exist -- retrieve it
				 prospectRoot = content.getNode("customer");
			}

	                                 
			int custId = custRec+1; //assign a new id to the customer node
	                      
			//Store content from the client JSP in the JCR
			Node custNode = prospectRoot.addNode("customer"+firstName+lastName+custId,"nt:unstructured");
	               
			//make sure name of node is unique
			custNode.setProperty("id", custId);
			custNode.setProperty("firstName", firstName);
			custNode.setProperty("lastName", lastName);
			custNode.setProperty("address", address); 
			custNode.setProperty("desc", desc);
	                                    
			// Save the session changes and log out
			session.save();
	  		session.logout();
	  		return custId;
		}
	       
		catch(Exception  e){
			log.error("RepositoryException: " + e);
		}*/
		return 0 ;
	}
	      
	  
	/*
	 * Determines if the content/prospect node exists
	 * This method returns these values:
	 * -1 - if prospect does not exist
	 * 0 - if content/prospect node exists; however, contains no children
	 * number - the number of children that the content/customer node contains
	*/
	private int doesProspectExist(Node content)
	{
		try
		{
			int index = 0 ;
			int childRecs = 0 ;
	      
			java.lang.Iterable<Node> prospectNode = JcrUtils.getChildNodes(content, "customer");
			Iterator it = prospectNode.iterator();
	               
	 //only going to be 1 content/customer node if it exists
	if (it.hasNext())
	 {
	 //Count the number of child nodes to customer
	 Node customerRoot = content.getNode("customer");
	 Iterable itCust = JcrUtils.getChildNodes(customerRoot);
	 Iterator childNodeIt = itCust.iterator();
	              
	//Count the number of customer child nodes
	while (childNodeIt.hasNext())
	{
	 childRecs++;
	 childNodeIt.next();
	}
	 return childRecs;
	  }
	else
	return -1; //content/customer does not exist
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
	return 0;
	 }
 
 }