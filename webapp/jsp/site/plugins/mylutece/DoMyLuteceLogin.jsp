<jsp:useBean id="myluteceApp" scope="request" class="fr.paris.lutece.plugins.mylutece.web.MyLuteceApp" />
<jsp:include page="../../PortalHeader.jsp" />
<%@ page import="fr.paris.lutece.portal.web.LocalVariables" %>

<%
LocalVariables.setLocal( config, request, response );
try
{
	response.sendRedirect( response.encodeRedirectURL( myluteceApp.doLogin( request ) ) );
}
finally
{
	LocalVariables.setLocal( null, null, null );
}
%>
