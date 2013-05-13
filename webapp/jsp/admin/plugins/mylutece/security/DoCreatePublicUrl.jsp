<%@ page errorPage="../../../ErrorPage.jsp" %>

<jsp:useBean id="authenticationFilterJspBean" scope="session" class="fr.paris.lutece.plugins.mylutece.web.security.AuthenticationFilterJspBean" />

<%
	authenticationFilterJspBean.init( request, fr.paris.lutece.plugins.mylutece.web.security.AuthenticationFilterJspBean.RIGHT_MANAGE_AUTHENTICATION_FILTER) ; 
	response.sendRedirect( authenticationFilterJspBean.doCreatePublicUrl( request ) );
%>

