<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="org.apache.sling.commons.json.io.*,com.adobe.cq.*, java.io.BufferedReader, java.util.List, java.util.ArrayList" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String paths = request.getParameter("paths");

	com.adobe.cq.service.ProspectService prospectService = sling.getService(com.adobe.cq.service.ProspectService.class);

/*BufferedReader reader = request.getReader();
 	String linedata = reader.readLine();
	String textdata = "";

	while (linedata != null){
            textdata += linedata;
            linedata = reader.readLine();

    }*/

    prospectService.approveProspects(paths);


%>