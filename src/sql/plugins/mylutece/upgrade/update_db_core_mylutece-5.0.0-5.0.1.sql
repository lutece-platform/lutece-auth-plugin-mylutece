-- liquibase formatted sql
-- changeset mylutece:update_db_core_mylutece-5.0.0-5.0.1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment Legacy XSL style tables left the core for plugin-xmltransformer and are absent from many databases: skip instead of failing the whole update
-- precondition-sql-check expectedResult:3 SELECT COUNT(1) from INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=database() AND TABLE_NAME IN ('core_style_mode_stylesheet','core_stylesheet','core_style');
--
-- The MyLutece portlet is now rendered with an HTML template (PortletHtmlContent)
-- instead of XML/XSL : remove the obsolete style and stylesheet
--
DELETE FROM core_style_mode_stylesheet WHERE id_style = 200 AND id_stylesheet = 310;
DELETE FROM core_stylesheet WHERE id_stylesheet = 310;
DELETE FROM core_style WHERE id_style = 200;
