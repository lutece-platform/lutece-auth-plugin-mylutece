<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="portletMyLutece" scope="session" class="fr.paris.lutece.plugins.mylutece.web.portlet.MyLutecePortletJspBean" />


<% portletMyLutece.init( request, portletMyLutece.RIGHT_MANAGE_ADMIN_SITE ); %>
<%= portletMyLutece.getCreate ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>