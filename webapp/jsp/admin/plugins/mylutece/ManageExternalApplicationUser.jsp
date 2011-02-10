<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="ExternalApplication" scope="session" class="fr.paris.lutece.plugins.mylutece.web.ExternalApplicationJspBean" />

<% 	ExternalApplication.init( request, ExternalApplication.RIGHT_MANAGE_EXTERNAL_APPLICATION ); %>
<%= ExternalApplication.getManageApplications( request ) %>

<%@ include file="../../AdminFooter.jsp" %>