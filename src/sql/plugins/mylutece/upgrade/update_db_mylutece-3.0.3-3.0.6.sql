INSERT INTO core_user_right (id_right,id_user) VALUES ('MYLUTECE_MANAGE_AUTHENTICATION_FILTER',1);
INSERT INTO core_admin_right VALUES ('MYLUTECE_MANAGE_AUTHENTICATION_FILTER', 'mylutece.adminFeature.mylutece_management_authentication_filter.name', 2, 'jsp/admin/plugins/mylutece/security/ManageAuthenticationFilter.jsp', 'mylutece.adminFeature.mylutece_management_authentication_filter.description', 0, 'mylutece', 'USERS', NULL, NULL, NULL);
INSERT INTO core_user_role (role_key,id_user) VALUES ('mylutece_manager',1);
--
-- Init Dashboard
--
INSERT INTO core_admin_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('myluteceAuthenticationFilterAdminDashboardComponent', 1, 3);
--
-- Init Public URLs
--

INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.login.page','jsp/site/Portal.jsp?page=mylutece&action=login');
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.doLogin','jsp/site/plugins/mylutece/DoMyLuteceLogin.jsp');
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.doLogout','jsp/site/plugins/mylutece/DoMyLuteceLogout.jsp');
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.createAccount.page','jsp/site/Portal.jsp?page=mylutece&action=createAccount');
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.lostPassword.page','jsp/site/Portal.jsp?page=mylutece&action=lostPassword');
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.lostLogin.page','jsp/site/Portal.jsp?page=mylutecedatabase&action=lostLogin');
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.doActionsAll','jsp/site/plugins/mylutece/Do*');
