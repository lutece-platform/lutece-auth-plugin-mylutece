<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean"%>

${ myluteceAttributeJspBean.init( pageContext.request, AttributeJspBean.RIGHT_MANAGE_MYLUTECE ) }
${ myluteceAttributeJspBean.getCreateAttribute( pageContext.request ) }

<%@ include file="../../../AdminFooter.jsp" %>
