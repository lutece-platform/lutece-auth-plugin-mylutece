<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="ExternalApplication" scope="session" class="fr.paris.lutece.plugins.mylutece.web.ExternalApplicationJspBean" />

<% 	ExternalApplication.init( request, ExternalApplication.RIGHT_MANAGE_EXTERNAL_APPLICATION );
    response.sendRedirect( ExternalApplication.doApplicationRedirect( request ) );
%>
