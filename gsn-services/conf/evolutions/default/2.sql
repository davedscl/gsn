# --- !Ups
INSERT INTO security_role(id, role_name) VALUES (1, 'user');
INSERT INTO security_role(id, role_name) VALUES (2, 'admin');
INSERT INTO users(id, email, name, first_name, last_name, active, email_validated) VALUES (1, 'root@localhost', 'Admin', 'Admin', '', True, True);
INSERT INTO users(id, email, name, first_name, last_name, active, email_validated) VALUES (2, 'test@localhost', 'test', 'test', '', True, True);
INSERT INTO users_security_role(users_id, security_role_id) VALUES (1, 1);
INSERT INTO users_security_role(users_id, security_role_id) VALUES (2, 1);
INSERT INTO users_security_role(users_id, security_role_id) VALUES (1, 2);
INSERT INTO linked_account(id, user_id, provider_user_id, provider_key) VALUES (1,1,'$2a$10$lRUloW/0IZTsQ1SUi7yFj.bZjSNw9MwrHR3h9VZZvafuPmzNNi0aq','password');
INSERT INTO client(id, name, client_id, secret, redirect, linked, user_id) VALUES (1, 'Default Web UI', 'web-gui-public', 'web-gui-secret', 'http://localhost:8000/profile/', True,1);

# --- !Downs

DELETE FROM linked_account WHERE id = 1;
DELETE FROM users_security_role WHERE security_role_id = 1 or security_role_id = 2;
DELETE FROM users WHERE id = 1;
DELETE FROM security_role WHERE id = 1 OR id = 2;