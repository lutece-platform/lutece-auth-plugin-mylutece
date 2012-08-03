ALTER TABLE mylutece_attribute ADD COLUMN anonymize SMALLINT DEFAULT NULL ;

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