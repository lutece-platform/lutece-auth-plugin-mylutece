<%@ page errorPage="../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.mylutece.web.security.AuthenticationFilterJspBean"%>

${ authenticationFilterJspBean.init( pageContext.request, AuthenticationFilterJspBean.RIGHT_MANAGE_AUTHENTICATION_FILTER ) }
${ pageContext.response.sendRedirect( authenticationFilterJspBean.doChangeUseAuthenticationRequired( pageContext.request )) }