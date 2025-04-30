<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.mylutece.web.security.AuthenticationFilterJspBean"%>

${ authenticationFilterJspBean.init( pageContext.request, AuthenticationFilterJspBean.RIGHT_MANAGE_AUTHENTICATION_FILTER ) }
${ authenticationFilterJspBean.getManageAdvancedParameters( pageContext.request ) }

<%@ include file="../../../AdminFooter.jsp" %>