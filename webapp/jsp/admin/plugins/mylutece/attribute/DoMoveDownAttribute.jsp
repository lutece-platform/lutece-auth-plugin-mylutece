<%@ page errorPage="../../../ErrorPage.jsp" %>

<jsp:useBean id="myLuteceAttribute" scope="session" class="fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean" />

<%
	myLuteceAttribute.init( request, fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean.RIGHT_MANAGE_MYLUTECE ) ; 
	response.sendRedirect( myLuteceAttribute.doMoveDownAttribute( request ) );  
%>

