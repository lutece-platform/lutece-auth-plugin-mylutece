<jsp:useBean id="managemylutece" scope="session" class="fr.paris.lutece.plugins.mylutece.web.ManageMyLuteceJspBean" />
<% String strContent = managemylutece.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
