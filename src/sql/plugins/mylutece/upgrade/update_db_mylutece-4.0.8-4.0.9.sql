-- liquibase formatted sql
-- changeset mylutece:update_db_mylutece-4.0.8-4.0.9.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM core_datastore WHERE entity_key='mylutece.site_property.default_role_keys'
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.site_property.default_role_keys','');
