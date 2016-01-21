package com.adobe.cq.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
  
import javax.jcr.NodeIterator;
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
 
 
import org.apache.sling.api.resource.LoginException;
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
			sqlStatement += " AND [nt:unstructured].isBlacklist IS NOT NULL AND [nt:unstructured].isBlacklist='true' ";
			sqlStatement += " AND [nt:unstructured].regStatusEmailType IS NOT NULL AND [nt:unstructured].regStatusEmailType='EXPIRED' ";
			sqlStatement += " AND [nt:unstructured].isApproved IS NOT NULL AND [nt:unstructured].isApproved='-1'";
			         
			javax.jcr.query.Query query = queryManager.createQuery(sqlStatement,"JCR-SQL2");
			 
			//Execute the query and get the results ...
			javax.jcr.query.QueryResult result = query.execute();
			 
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
	
	 
	 private Node nodeSearch(Node node, String property, String value){
		 
		 try{
			 
			 if (node.hasProperty(property) && node.getProperty(property).getString().equals(value)){
				 
				 //Conditions below ensure this node is one that has met prospect query criteria before returning it
				 //NOTE - Needs to be removed once way of uniquely identifying prospect found
				 boolean isNotApproved = (node.hasProperty("isApproved") && node.getProperty("isApproved").getString().equals("-1"));
				 boolean isNodeBlacklist = (node.hasProperty("isBlacklist") && node.getProperty("isBlacklist").getString().equals("true"));
				 boolean isRegStatusExpired = (node.hasProperty("regStatusEmailType") && node.getProperty("regStatusEmailType").getString().equals("EXPIRED"));
				 if (isNotApproved && isNodeBlacklist && isRegStatusExpired) {
					return node; 
				 }
			 }
		 
			 NodeIterator nodeIterator = node.getNodes();
			 
			 while (nodeIterator.hasNext()){
				 nodeSearch(nodeIterator.nextNode(), property, value);
			 }
			 
		 } catch (RepositoryException re) {
			 log.error("Node search error", re);
		 }
		 
		 return null;
	 }
	 
	//Stores prospect data in the Adobe CQ JCR
	public int approveProspect(String email)
	{      
		try {
	              
		    //Invoke the adaptTo method to create a Session used to create a QueryManager
			ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
		    session = resourceResolver.adaptTo(Session.class);
	               
		    //Create a node that represents the root node
		    Node root = session.getRootNode();
			                     
			//Get the prospects node in the JCR
			Node prospectsNode = root.getNode("var").getNode("prospects");
			
			//Find target node
			Node targetNode = null;
			if (prospectsNode != null){
				targetNode = nodeSearch(prospectsNode, "email", email);
			}
			
			//Return in case target not found
			if (targetNode == null){
				log.info("No nodes found with email  '" + email + "'");
				return -2;
			}
	                      
			//Store content from the client JSP in the JCR
			targetNode.setProperty("isApproved", "1");
	                                    
			// Save the session changes and log out
			session.save();
	  		session.logout();
	  		return 1;
		}
	       
		catch(RepositoryException  e){
			log.error("RepositoryException: " + e);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			log.error("LoginException: " + e);
		}
		return -1;
	}
 
 }