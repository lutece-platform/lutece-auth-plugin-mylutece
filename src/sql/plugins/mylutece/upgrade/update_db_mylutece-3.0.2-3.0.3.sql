--liquibase formatted sql
--changeset mylutece:update_db_mylutece-3.0.2-3.0.3.sql
--preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE mylutece_attribute ADD COLUMN anonymize SMALLINT DEFAULT NULL ;

DROP TABLE IF EXISTS mylutece_user_anonymize_field;
CREATE  TABLE mylutece_user_anonymize_field (
  field_name VARCHAR(100) NOT NULL ,
  anonymize SMALLINT NOT NULL ,
  PRIMARY KEY (field_name)
  );
  
DROP TABLE IF EXISTS mylutece_connections_log;
CREATE TABLE mylutece_connections_log (
	ip_address varchar(63) default NULL,
	date_login timestamp default CURRENT_TIMESTAMP NOT NULL,
	login_status int default NULL
);