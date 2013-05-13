<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<jsp:useBean id="authenticationFilterJspBean" scope="session" class="fr.paris.lutece.plugins.mylutece.web.security.AuthenticationFilterJspBean" />

<%
	authenticationFilterJspBean.init( request, fr.paris.lutece.plugins.mylutece.web.security.AuthenticationFilterJspBean.RIGHT_MANAGE_AUTHENTICATION_FILTER ) ;
%>
<%= authenticationFilterJspBean.getManageAdvancedParameters( request ) %>

<%@ include file="../../../AdminFooter.jsp" %>