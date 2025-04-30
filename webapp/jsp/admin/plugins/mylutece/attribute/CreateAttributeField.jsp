<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean"%>

${ myluteceAttributeFieldJspBean.init( pageContext.request, AttributeJspBean.RIGHT_MANAGE_MYLUTECE ) }
${ myluteceAttributeFieldJspBean.getCreateAttributeField( pageContext.request ) }

<%@ include file="../../../AdminFooter.jsp" %>
