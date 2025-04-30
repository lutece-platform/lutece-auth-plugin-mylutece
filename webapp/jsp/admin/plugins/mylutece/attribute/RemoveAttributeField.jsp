<%@ page errorPage="../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean"%>

${ myluteceAttributeFieldJspBean.init( pageContext.request, AttributeJspBean.RIGHT_MANAGE_MYLUTECE ) }
${ pageContext.response.sendRedirect( myluteceAttributeFieldJspBean.doConfirmRemoveAttributeField( pageContext.request )) }