<jsp:include page="../../PortalHeader.jsp" />
<%@ page import="fr.paris.lutece.portal.web.LocalVariables" %>
<%@page import="jakarta.inject.Inject"%>
<%@page import="fr.paris.lutece.plugins.mylutece.web.MyLuteceApp"%>

<%! @Inject private MyLuteceApp myLuteceApp; %>
<%
LocalVariables.setLocal( config, request, response );
try
{
	response.sendRedirect( response.encodeRedirectURL( myLuteceApp.doLogin( request ) ) );
}
finally
{
	LocalVariables.setLocal( null, null, null );
}
%>
