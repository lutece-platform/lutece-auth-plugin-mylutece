-- liquibase formatted sql
-- changeset mylutece:init_core_mylutece.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- Dumping data for table core_portlet_type
--
INSERT INTO core_portlet_type (id_portlet_type,name,url_creation,url_update,home_class,plugin_name,url_docreate,create_script,create_specific,create_specific_form,url_domodify,modify_script,modify_specific,modify_specific_form) VALUES
    ('MYLUTECE_PORTLET','mylutece.portlet.name','plugins/mylutece/CreatePortletMyLutece.jsp','plugins/mylutece/ModifyPortletMyLutece.jsp','fr.paris.lutece.plugins.mylutece.business.portlet.MyLutecePortletHome','mylutece','plugins/mylutece/DoCreatePortletMyLutece.jsp','/admin/portlet/script_create_portlet.html','','','plugins/mylutece/DoModifyPortletMyLutece.jsp','/admin/portlet/script_modify_portlet.html','','');

--
-- Dumping data for table core_admin_role
--
INSERT INTO core_admin_role (role_key,role_description) VALUES ('assign_roles','Assigner des roles aux utilisateurs');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('assign_groups','Assigner des groupes aux utilisateurs');
INSERT INTO core_admin_role (role_key,role_description) VALUES ('mylutece_manager','Gérer les patramètres avancés Mylutece');


--
-- Dumping data for table core_admin_role_resource
--
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES
    ('assign_roles','ROLE_TYPE','*','ASSIGN_ROLE');
INSERT INTO core_admin_role_resource (role_key,resource_type,resource_id,permission) VALUES
    ('mylutece_manager','MYLUTECE','*','*');


--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('assign_roles',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('assign_roles',2);
INSERT INTO core_user_role (role_key,id_user) VALUES ('assign_roles',3);
INSERT INTO core_user_role (role_key,id_user) VALUES ('assign_groups',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('assign_groups',2);
INSERT INTO core_user_role (role_key,id_user) VALUES ('assign_groups',3);
INSERT INTO core_user_role (role_key,id_user) VALUES ('mylutece_manager',1);
--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order) VALUES ('MYLUTECE_MANAGEMENT', 'mylutece.adminFeature.mylutece_management.name', 2, 'jsp/admin/plugins/mylutece/ManageMylutece.jsp', 'mylutece.adminFeature.mylutece_management.description', 0, 'mylutece', 'USERS', NULL, NULL, NULL);
INSERT INTO core_admin_right (id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order) VALUES ('MYLUTECE_MANAGE_AUTHENTICATION_FILTER', 'mylutece.adminFeature.mylutece_management_authentication_filter.name', 2, 'jsp/admin/plugins/mylutece/security/ManageAuthenticationFilter.jsp', 'mylutece.adminFeature.mylutece_management_authentication_filter.description', 0, 'mylutece', 'USERS', NULL, NULL, NULL);

--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('MYLUTECE_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('MYLUTECE_MANAGE_AUTHENTICATION_FILTER',1);


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
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.modifyAccount.page','jsp/site/Portal.jsp?page=mylutece&action=modifyAccount');
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.lostPassword.page','jsp/site/Portal.jsp?page=mylutece&action=lostPassword');
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.lostLogin.page','jsp/site/Portal.jsp?page=mylutecedatabase&action=lostLogin');
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.doActionsAll','jsp/site/plugins/mylutece/Do*');
