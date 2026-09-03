-- liquibase formatted sql
-- changeset mylutece:update_db_core_mylutece-5.0.0-5.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
--
-- The MyLutece portlet is now rendered with an HTML template (PortletHtmlContent)
-- instead of XML/XSL : remove the obsolete style and stylesheet
--
DELETE FROM core_style_mode_stylesheet WHERE id_style = 200 AND id_stylesheet = 310;
DELETE FROM core_stylesheet WHERE id_stylesheet = 310;
DELETE FROM core_style WHERE id_style = 200;
