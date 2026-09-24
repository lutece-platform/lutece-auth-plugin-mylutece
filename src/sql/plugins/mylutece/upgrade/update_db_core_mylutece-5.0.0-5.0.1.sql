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

--
-- The FreeMarker template of the MyLutece portlet is now managed by the core (core_portlet_template, core_portlet.id_template,
-- "Gestion des modèles de rubrique" feature). It replaces the XSL style 'Rubrique MyLutece - Défaut' (id_style 200).
--
-- The plugin upgrade scripts run BEFORE the core upgrade script in the same liquibase run (sql/plugins/* sorts before sql/upgrade/*) :
-- the core structures are created here when they do not exist yet, with the very same statements as the core script, which is then skipped.
--

-- changeset mylutece:update_db_core_mylutece-5.0.0-5.0.1.sql-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = database() AND table_name = 'core_portlet' AND column_name = 'id_template'
ALTER TABLE core_portlet ADD COLUMN id_template int default 0 NOT NULL;

-- changeset mylutece:update_db_core_mylutece-5.0.0-5.0.1.sql-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = database() AND table_name = 'core_portlet_template'
CREATE TABLE IF NOT EXISTS core_portlet_template (
	id_template int AUTO_INCREMENT NOT NULL,
	id_portlet_type varchar(50) default NULL,
	description varchar(255) default NULL,
	template_path varchar(255) default NULL,
	PRIMARY KEY (id_template)
);

-- changeset mylutece:update_db_core_mylutece-5.0.0-5.0.1.sql-rev3.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_portlet_template WHERE id_portlet_type = 'MYLUTECE_PORTLET'
INSERT INTO core_portlet_template (id_portlet_type, description, template_path) VALUES ('MYLUTECE_PORTLET', 'Rubrique MyLutece - Défaut', 'skin/plugins/mylutece/portlet/portlet_mylutece.html');

-- changeset mylutece:update_db_core_mylutece-5.0.0-5.0.1.sql-rev4.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The portlets rendered with the old XSL style use the shipped template, and no longer reference a style
UPDATE core_portlet SET id_template = (
		SELECT MIN(ct.id_template) FROM core_portlet_template ct
		WHERE ct.id_portlet_type = 'MYLUTECE_PORTLET' AND ct.template_path = 'skin/plugins/mylutece/portlet/portlet_mylutece.html' )
	WHERE id_portlet_type = 'MYLUTECE_PORTLET' AND id_style = 200 AND id_template = 0;
UPDATE core_portlet SET id_style = 0 WHERE id_portlet_type = 'MYLUTECE_PORTLET';
