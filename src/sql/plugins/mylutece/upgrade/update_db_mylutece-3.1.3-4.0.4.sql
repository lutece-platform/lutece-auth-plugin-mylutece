--liquibase formatted sql
--changeset mylutece:update_db_mylutece-3.1.3-4.0.4.sql
--preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right set admin_url='jsp/admin/plugins/mylutece/ManageMylutece.jsp' WHERE id_right='MYLUTECE_MANAGEMENT';