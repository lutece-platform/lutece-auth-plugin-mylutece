<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.mylutece.web.portlet.MyLutecePortletJspBean"%>

${ myLutecePortletJspBean.init( pageContext.request, MyLutecePortletJspBean.RIGHT_MANAGE_ADMIN_SITE ) }
${ myLutecePortletJspBean.getModify( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
