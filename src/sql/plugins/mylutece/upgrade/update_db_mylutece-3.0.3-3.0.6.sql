INSERT INTO core_user_right (id_right,id_user) VALUES ('MYLUTECE_MANAGE_AUTHENTICATION_FILTER',1);
INSERT INTO core_admin_right VALUES ('MYLUTECE_MANAGE_AUTHENTICATION_FILTER', 'mylutece.adminFeature.mylutece_management_authentication_filter.name', 2, 'jsp/admin/plugins/mylutece/security/ManageAuthenticationFilter.jsp', 'mylutece.adminFeature.mylutece_management_authentication_filter.description', 0, 'mylutece', 'USERS', NULL, NULL, NULL);
INSERT INTO core_user_role (role_key,id_user) VALUES ('mylutece_manager',1);
--
-- Init Dashboard
--
INSERT INTO core_admin_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('myluteceAuthenticationFilterAdminDashboardComponent', 1, 3);