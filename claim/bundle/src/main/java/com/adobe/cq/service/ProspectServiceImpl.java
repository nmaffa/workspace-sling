package com.adobe.cq.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
  
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
  
import javax.jcr.NodeIterator;
import javax.jcr.Node; 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
   
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
  
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import javax.jcr.RepositoryException;
import org.apache.felix.scr.annotations.Reference;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.sling.api.resource.LoginException;
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory; 
import org.apache.sling.api.resource.ResourceResolver; 
//import org.apache.sling.api.resource.Resource; 

import com.adobe.cq.model.Prospect;
  
  
/**
 * @author nmaffa
 */
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
		
		try {
		           
		    //Invoke the adaptTo method to create a Session used to create a QueryManager
			ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
		    session = resourceResolver.adaptTo(Session.class);
		 
		    //Obtain the query manager for the session ...
		    QueryManager queryManager = session.getWorkspace().getQueryManager();
		     
			//Setup the query to get all prospect records where isBlacklist='true',
			//regStatusEmailType='EXPIRED', and isApproved='-1'
			String sqlStatement = "SELECT * FROM [nt:unstructured] WHERE ISDESCENDANTNODE([/var/prospects])";
			sqlStatement += " AND [nt:unstructured].isBlacklist IS NOT NULL AND [nt:unstructured].isBlacklist='true' ";
			sqlStatement += " AND [nt:unstructured].regStatusEmailType IS NOT NULL AND [nt:unstructured].regStatusEmailType='EXPIRED' ";
			sqlStatement += " AND [nt:unstructured].isApproved IS NOT NULL AND [nt:unstructured].isApproved='-1'";
			         
			//Create JCR-SQL2 query
			Query query = queryManager.createQuery(sqlStatement,"JCR-SQL2");
			 
			//Execute the query and get the results 
			QueryResult result = query.execute();
			 
			//Iterate over the nodes in the results
			NodeIterator nodeIter = result.getNodes();
	 
			while ( nodeIter.hasNext() ) {
				
				//Create prospect object and set properties based on result node
				prospect = new Prospect();
				Node prospectNode = nodeIter.nextNode();
				prospect.setJcrPath(prospectNode.getPath());
				//prospect.setUuid(prospectNode.getProperty("uuid").getString());
			    prospect.setEmail(prospectNode.getProperty("email").getString());
				prospect.setFname(prospectNode.getProperty("fname").getString());
				prospect.setLname(prospectNode.getProperty("lname").getString());
				prospect.setIsApproved(prospectNode.getProperty("isApproved").getString());
				 
				//Push prospect to the list
				prospectList.add(prospect);
			}                          
		} catch( RepositoryException e ) {
			log.error("Repository Exception occured while getting prospect data", e);
		} catch( Exception e ) {
			log.error("Exception occured while getting prospect data", e);
		}
		
		//Log out of session
		if (session!= null && session.isLive()){
			session.logout();
		}
		
		//Return String of XML representation of prospect data
		return convertToString(toXml(prospectList));
	}	      
	      
	//Convert Prospect data retrieved from the AEM JCR
	//into an XML schema to pass back to client
	private Document toXml(List<Prospect> prospectList) {
		try
		{
		    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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
		         
		         //Set UUID element
	//	         Element uuidElement = doc.createElement( "uuid" );
	//	         uuidElement.appendChild( doc.createTextNode( prospect.getUuid() ) );
	//	         prospectElement.appendChild( uuidElement );
		        
		         //Set JCR Path element
		         Element jcrPathElement = doc.createElement("jcrpath");
		         jcrPathElement.appendChild( doc.createTextNode( prospect.getJcrPath() ) );
		         prospectElement.appendChild( jcrPathElement );
		         
		         //Set Email element
		         Element emailElement = doc.createElement( "email" );
		         emailElement.appendChild( doc.createTextNode( prospect.getEmail() ) );
		         prospectElement.appendChild( emailElement );
		                                                           
		         //Set First Name element
		         Element fNameElement = doc.createElement( "fname" );
		         fNameElement.appendChild( doc.createTextNode( prospect.getFname() ) );
		         prospectElement.appendChild( fNameElement );
		                     
		         //Set Last name element
		         Element lNameElement = doc.createElement( "lname" );
		         lNameElement.appendChild( doc.createTextNode( prospect.getLname() ) );
		         prospectElement.appendChild( lNameElement );
		                    
		      }
		             
		return doc;
		
		}
		catch(Exception e)
		{
		    log.error("Exception occured while making XML ", e);
		}
		return null;
	}
	  
	//Convert XML to String
	private String convertToString(Document xml)
	{
		if (xml == null){
			return "XML is null";
		}
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(xml);
			transformer.transform(source, result);
			return result.getWriter().toString();
			
		} catch(Exception e) {
			log.error("Error converting XML to String", e);
		}
		
		return "Failure in converting XML to String";
	  
	 }
	
	//Reads in String of JCR Node Paths delimited by ','
	//For each JCR Node Path, sets node "isApproved" attribute to "1"
	//Returns 1 if all nodes found and marked as approved successfully, -1 otherwise
	public int approveProspects(String idString){
		
		int finalCode = 1;
		String[] ids = idString.split(",");

		try{
		
			//Invoke the adaptTo method to create a Session used to create a QueryManager
			ResourceResolver resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
		    session = resourceResolver.adaptTo(Session.class);
		
			for (String id : ids){
				
				    //Create a node that represents the root node
				    Node root = session.getRootNode();
					                     
					//Get the prospects node in the JCR
					Node prospectsNode = root.getNode("var").getNode("prospects");
					
					//Find target node
					Node targetNode = null;
					if (prospectsNode != null){
						//targetNode = nodeSearch(prospectsNode, "jcr:path", id);
						targetNode = nodePathSearch(prospectsNode, id);
					}
					
					//If target node is not found,
					if (targetNode == null){
						log.info("No nodes found with ID  '" + id + "'");
						finalCode=-1;
					} else {
						//Store content from the client JSP in the JCR
						targetNode.setProperty("isApproved", "1");
					}

			}
		
			// Save the session changes
			session.save();
	  		
		}
		
		catch(RepositoryException  e){
			log.error("RepositoryException: ", e);
			finalCode = -1;
		} catch (LoginException e) {
			log.error("LoginException: ", e);
			finalCode = -1;
		} catch (Exception e) {
			log.error("Exception occured while approving prospect ", e);
			finalCode = -1;
		}
		
		if (session != null && session.isLive()){
			session.logout();
		}
		
		return finalCode;
	}
	 
	//Does a Depth First Search on a given root node for a node with provided path value
	private Node nodePathSearch(Node node, String pathValue){

		try{
			//Return current node if path matches pathValue
			 if (node.getPath().equals(pathValue)){	 
				 return node;
			 }
		 
			 //Iterate through node's children to find node with matching pathValue
			 NodeIterator nodeIterator = node.getNodes();		 
			 while (nodeIterator.hasNext()){
				 Node n = nodePathSearch(nodeIterator.nextNode(), pathValue);
				 if (n != null){
					 return n;
				 }
			 }
			 
		 } catch (RepositoryException re) {
			 log.error("Repository Exception occured while searching for nodes", re);
		 } catch (Exception e){
			 log.error("Exception occured while searching for nodes", e);
		 }
		 
		 return null;
		
	}
	
	//Does a Depth First Search on a given root node for a node with provided property/value pair.
	//Method is unused for now as we are doing a path based search for nodes rather than a property
	//based search.
	 private Node nodeSearch(Node node, String property, String value){
		 
		 try{
			 
			 if (node.hasProperty(property) && node.getProperty(property).getString().equals(value)){
				 
				 return node;
			 }
		 
			 NodeIterator nodeIterator = node.getNodes();
			 
			 while (nodeIterator.hasNext()){
				 Node n = nodeSearch(nodeIterator.nextNode(), property, value);
				 if (n != null){
					 return n;
				 }
			 }
			 
		 } catch (RepositoryException re) {
			 log.error("Repository Exception occured while searching for nodes", re);
		 } catch (Exception e){
			 log.error("Exception occured while searching for nodes", e);
		 }
		 
		 return null;
	 }
 
 }