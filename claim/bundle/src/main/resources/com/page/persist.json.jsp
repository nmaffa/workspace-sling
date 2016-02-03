<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="org.apache.sling.commons.json.io.*,com.adobe.cq.*, java.io.BufferedReader, java.util.List, java.util.ArrayList" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String paths = request.getParameter("paths");

	com.adobe.cq.service.ProspectService prospectService = sling.getService(com.adobe.cq.service.ProspectService.class);

    prospectService.approveProspects(paths);


%>