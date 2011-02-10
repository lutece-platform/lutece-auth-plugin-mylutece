<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<jsp:useBean id="myLuteceAttributeField" scope="session" class="fr.paris.lutece.plugins.mylutece.web.attribute.AttributeFieldJspBean" />

<% myLuteceAttributeField.init( request, fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean.RIGHT_MANAGE_MYLUTECE ) ; %>
<%= myLuteceAttributeField.getCreateAttributeField ( request ) %>

<%@ include file="../../../AdminFooter.jsp" %>
