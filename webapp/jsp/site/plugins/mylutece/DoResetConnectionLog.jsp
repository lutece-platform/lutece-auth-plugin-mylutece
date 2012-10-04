<jsp:useBean id="myluteceApp" scope="request" class="fr.paris.lutece.plugins.mylutece.web.MyLuteceApp" />
<%@ page import="fr.paris.lutece.portal.web.LocalVariables" %>
<%
LocalVariables.setLocal( config, request, response );
response.sendRedirect( myluteceApp.doResetConnectionLog( request ) );
%>
