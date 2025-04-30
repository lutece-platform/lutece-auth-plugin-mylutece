<jsp:include page="../../PortalHeader.jsp" />
<%@page import="jakarta.inject.Inject"%>
<%@page import="fr.paris.lutece.plugins.mylutece.web.MyLuteceApp"%>

<%! @Inject private MyLuteceApp myLuteceApp; %>
<%
	response.sendRedirect( response.encodeRedirectURL( myLuteceApp.doLogout( request ) ) );
%>
