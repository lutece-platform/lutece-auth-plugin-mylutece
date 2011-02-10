<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="portletMyLutece" scope="session" class="fr.paris.lutece.plugins.mylutece.web.portlet.MyLutecePortletJspBean" />

<%
    portletMyLutece.init( request, portletMyLutece.RIGHT_MANAGE_ADMIN_SITE );
    response.sendRedirect( portletMyLutece.doModify( request )   );
%>