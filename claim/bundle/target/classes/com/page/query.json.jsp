<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="org.apache.sling.commons.json.io.*,org.w3c.dom.*" %><%
    //String filter = request.getParameter("filter");

com.adobe.cq.service.ProspectService prospectService = sling.getService(com.adobe.cq.service.ProspectService.class);

String xmlProspectData = prospectService.getProspects() ; 

//Send the data back to the client 
JSONWriter writer = new JSONWriter(response.getWriter());
writer.object();
writer.key("xml");
writer.value(xmlProspectData);

writer.endObject();
%>