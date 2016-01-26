package com.adobe.cq.sling;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.ServerException;
import java.util.Dictionary;
 
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.osgi.OsgiUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.felix.scr.annotations.Reference;
import org.osgi.service.component.ComponentContext;
import javax.jcr.Session;
import javax.jcr.Node; 
import org.json.simple.JSONObject;
import java.util.UUID;
 
@SlingServlet(paths="/bin/handleApprovalServlet", methods = "POST", metatype=true)
public class HandleApproval extends org.apache.sling.api.servlets.SlingAllMethodsServlet {
     private static final long serialVersionUID = 2598426539166789516L;
      
     @Reference
     private SlingRepository repository;
      
     public void bindRepository(SlingRepository repository) {
            this.repository = repository; 
            }
           
     @Override
     protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServerException, IOException {
       
      try
      {
         //Get the submitted form data that is sent from the
              //CQ web page  
    	  String email = request.getParameter("email");
    	  String isApproved = request.getParameter("isApproved");
    	  BufferedReader reader = request.getReader();
//          String id = UUID.randomUUID().toString();
//          String firstName = request.getParameter("firstName");
//          String lastName = request.getParameter("lastName");
//          String address = request.getParameter("address");
//          String cat = request.getParameter("cat");
//          String state = request.getParameter("state");
//          String details = request.getParameter("details");
//          String date = request.getParameter("date"); 
//          String city = request.getParameter("city"); 
       
          //Encode the submitted form data to JSON
          JSONObject obj=new JSONObject();
          obj.put("email", email);
          obj.put("isApproved", isApproved);
//          obj.put("id",id);
//          obj.put("firstname",firstName);
//          obj.put("lastname",lastName);
//          obj.put("address",address);
//          obj.put("cat",cat);
//          obj.put("state",state);
//          obj.put("details",details);
//          obj.put("date",date);
//          obj.put("city",city);
           
             //Get the JSON formatted data    
          String jsonData = obj.toJSONString();
           
             //Return the JSON formatted data
         response.getWriter().write(jsonData);
      }
      catch(Exception e)
      {
          e.printStackTrace();
      }
    }
}