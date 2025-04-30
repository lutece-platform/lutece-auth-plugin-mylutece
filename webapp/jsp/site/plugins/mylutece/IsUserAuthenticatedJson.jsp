<%@page import="jakarta.inject.Inject"%>
<%@page import="fr.paris.lutece.plugins.mylutece.web.MyLuteceApp"%>

<%! @Inject private MyLuteceApp myLuteceApp; %>
<%= myLuteceApp.isUserAuthenticated(request) %>