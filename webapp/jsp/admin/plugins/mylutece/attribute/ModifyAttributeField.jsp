<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean"%>

${ myluteceAttributeFieldJspBean.init( pageContext.request, AttributeJspBean.RIGHT_MANAGE_MYLUTECE ) }
${ myluteceAttributeFieldJspBean.getModifyAttributeField( pageContext.request ) }

<%@ include file="../../../AdminFooter.jsp" %>
