<%@ page errorPage="../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean"%>

${ myluteceAttributeJspBean.init( pageContext.request, AttributeJspBean.RIGHT_MANAGE_MYLUTECE ) }
${ pageContext.response.sendRedirect( myluteceAttributeJspBean.doCreateAttribute( pageContext.request )) }