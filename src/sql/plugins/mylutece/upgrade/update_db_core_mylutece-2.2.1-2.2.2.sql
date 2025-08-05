-- liquibase formatted sql
-- changeset mylutece:update_db_core_mylutece-2.2.1-2.2.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
DELETE FROM core_admin_right WHERE id_right = 'MYLUTECE_MANAGE_EXTERNAL_APPLICATION';
DELETE FROM core_user_right WHERE id_right = 'MYLUTECE_MANAGE_EXTERNAL_APPLICATION';
