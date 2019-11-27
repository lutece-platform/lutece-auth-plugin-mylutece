<jsp:useBean id="myluteceApp" scope="request" class="fr.paris.lutece.plugins.mylutece.web.MyLuteceApp" />
<%= myluteceApp.isUserAuthenticated(request) %>