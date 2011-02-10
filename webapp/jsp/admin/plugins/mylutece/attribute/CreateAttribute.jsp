<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<jsp:useBean id="myLuteceAttribute" scope="session" class="fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean" />

<% myLuteceAttribute.init( request, fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean.RIGHT_MANAGE_MYLUTECE ) ; %>
<%= myLuteceAttribute.getCreateAttribute ( request ) %>

<%@ include file="../../../AdminFooter.jsp" %>
