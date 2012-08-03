--
-- Table structure for table mylutece_attribute
--
DROP TABLE IF EXISTS mylutece_attribute;
CREATE TABLE mylutece_attribute (
	id_attribute INT DEFAULT 0 NOT NULL,
	type_class_name VARCHAR(255) DEFAULT NULL,
	title LONG VARCHAR DEFAULT NULL,
	help_message LONG VARCHAR DEFAULT NULL,
	is_mandatory SMALLINT DEFAULT 0,
	is_shown_in_search SMALLINT DEFAULT 0,
	attribute_position INT DEFAULT 0,
	plugin_name VARCHAR(255) DEFAULT NULL,
	anonymize SMALLINT DEFAULT NULL,
	PRIMARY KEY (id_attribute)
);

--
-- Table structure for table mylutece_attribute_field
--
DROP TABLE IF EXISTS mylutece_attribute_field;
CREATE TABLE mylutece_attribute_field (
	id_field INT DEFAULT 0 NOT NULL,
	id_attribute INT DEFAULT NULL,
	title VARCHAR(255) DEFAULT NULL,
	DEFAULT_value LONG VARCHAR DEFAULT NULL,
	is_DEFAULT_value SMALLINT DEFAULT 0,
	height INT DEFAULT NULL,
	width INT DEFAULT NULL,
	max_size_enter INT DEFAULT NULL,
	is_multiple SMALLINT DEFAULT 0,
	field_position INT DEFAULT NULL,
	PRIMARY KEY (id_field)
);

--
-- Table structure for table mylutece_admin_user_field
--
DROP TABLE IF EXISTS mylutece_user_field;
CREATE TABLE mylutece_user_field (
	id_user_field INT DEFAULT 0 NOT NULL,
	id_user INT DEFAULT NULL,
	id_attribute INT DEFAULT NULL,
	id_field INT DEFAULT NULL,
	user_field_value LONG VARCHAR DEFAULT NULL,
	PRIMARY KEY (id_user_field)
);

DROP TABLE IF EXISTS mylutece_user_anonymize_field;
CREATE  TABLE mylutece_user_anonymize_field (
  field_name VARCHAR(100) NOT NULL ,
  anonymize BIT NOT NULL ,
  PRIMARY KEY (field_name)
  );

DROP TABLE IF EXISTS mylutece_connections_log;
CREATE TABLE mylutece_connections_log (
	ip_address varchar(63) default NULL,
	date_login timestamp default CURRENT_TIMESTAMP NOT NULL,
	login_status int default NULL
);