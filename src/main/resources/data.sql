-- Privileges
INSERT INTO privileges(privilege_id, privilege_name) VALUES (1, "canRead");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (2, "canRecord");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (3, "canAddRequest");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (4, "canAlterRequest");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (5, "canQcRecording");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (6, "canManageTechnicalMetadata");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (7, "canGetClaimReport");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (8, "canManageUser");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (9, "canGetUserReport");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (10, "canGetAlbumReport");
INSERT INTO privileges(privilege_id, privilege_name) VALUES (11, "canGetQualityReport");
-- Roles
INSERT INTO roles(role_id, role_name) VALUES(1, "ADMIN");
INSERT INTO roles(role_id, role_name) VALUES(2, "EDITOR");
INSERT INTO roles(role_id, role_name) VALUES(3, "ENGINEER");
-- Role To Privileges
-- ADMIN
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,1);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,2);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,3);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,4);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,5);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,6);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,7);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,8);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,9);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,10);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (1,11);
--EDITOR
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (2,1);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (2,3);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (2,4);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (2,5);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (2,6);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (2,7);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (2,9);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (2,10);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (2,11);
--ENGINEER
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (3,1);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (3,2);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (3,5);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (3,6);
INSERT INTO role_to_privileges(role_role_id, privilege_privilege_id) VALUES (3,7);

-- User
INSERT INTO users(username, password, nice_name, date_of_birth, street, number,postal_code, city, country, telephone_number, is_active, role_id) VALUES ("admin@wilbertbongers.nl", "$2a$12$GXWgb7P0FJH3off.2xj9VObpwVZ.xbyWYBHarQB9NNY8Omx6bTCKm", "admin user", "2001-01-01", "wegisweg", 10, "1234AA", "Rotterdam", "Nederland","010123456789",  true, 1);
INSERT INTO users(username, password, nice_name, date_of_birth, street, number,postal_code, city, country, telephone_number, is_active, role_id) VALUES ("editor@wilbertbongers.nl", "$2a$12$2tlBFTj8RX8KjuAe8xGpVe5lkwmAYm/wFvfOHCQfs.LzmapOlMiy2", "editor user", "2001-02-02", "wegisweg", 10, "1234AA", "Rotterdam", "Nederland","010123456789",  true, 2);
INSERT INTO users(username, password, nice_name, date_of_birth, street, number,postal_code, city, country, telephone_number, is_active, role_id) VALUES ("engineer@wilbertbongers.nl", "$2a$12$9FCmNhOnNf1iY3SPBXhqpu1lSJlLdABZb1n7pSuUm8Fj6w44LlNdK", "engineer user", "2001-03-03", "wegisweg", 10, "1234AA", "Rotterdam", "Nederland","010123456789",  true, 3);
-- Recording Technologies
INSERT INTO recording_technologies(slug, description, samplerate, bitdepth) VALUES ("16-44", "16bit 44.1Khz", 44100, 16);
INSERT INTO recording_technologies(slug, description, samplerate, bitdepth) VALUES ("16-48", "16bit 48Khz", 48000, 16);
INSERT INTO recording_technologies(slug, description, samplerate, bitdepth) VALUES ("16-96","16bit 96Khz", 96000, 16);
INSERT INTO recording_technologies(slug, description, samplerate, bitdepth) VALUES ("24-44","24bit 44.1Khz", 44100, 24);
INSERT INTO recording_technologies(slug, description, samplerate, bitdepth) VALUES ("24-48","24bit 48Khz", 48000, 24);
INSERT INTO recording_technologies(slug, description, samplerate, bitdepth) VALUES ("24-96","24bit 96Khz", 96000, 24);