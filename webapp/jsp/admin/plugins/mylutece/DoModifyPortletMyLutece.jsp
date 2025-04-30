<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.mylutece.web.portlet.MyLutecePortletJspBean"%>

${ myLutecePortletJspBean.init( pageContext.request, MyLutecePortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ pageContext.response.sendRedirect( myLutecePortletJspBean.doModify( pageContext.request )) }