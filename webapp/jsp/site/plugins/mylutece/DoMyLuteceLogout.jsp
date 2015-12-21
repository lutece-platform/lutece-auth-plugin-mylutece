<jsp:useBean id="myluteceApp" scope="request" class="fr.paris.lutece.plugins.mylutece.web.MyLuteceApp" />
<jsp:include page="../../PortalHeader.jsp" />

<%
	response.sendRedirect( response.encodeRedirectURL( myluteceApp.doLogout( request ) ) );
%>
