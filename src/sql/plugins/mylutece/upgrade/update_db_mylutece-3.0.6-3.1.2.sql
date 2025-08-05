-- liquibase formatted sql
-- changeset mylutece:update_db_mylutece-3.0.6-3.1.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_datastore(entity_key,entity_value) VALUES('mylutece.security.public_url.mylutece.url.modifyAccount.page','jsp/site/Portal.jsp?page=mylutece&action=modifyAccount');
