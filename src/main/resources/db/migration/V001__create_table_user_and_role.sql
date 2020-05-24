CREATE TABLE os_user (
	id SERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	email VARCHAR(50) NOT NULL,
	password VARCHAR(150) NOT NULL
);

CREATE TABLE os_role (
	id SERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL
);

CREATE TABLE os_user_role (
	id_user INTEGER NOT NULL,
	id_role INTEGER NOT NULL,
	PRIMARY KEY (id_user, id_role),
	FOREIGN KEY (id_user) REFERENCES os_user(id),
	FOREIGN KEY (id_role) REFERENCES os_role(id)
);

INSERT INTO os_user (id, name, email, password) values (1, 'Administrador', 'admin@admin.com', '$2a$10$l4TKw2RXr1Put36T3qkEruXObOVGhqebp4g0f1HTAZ07smXDGkCG.');
INSERT INTO os_user (id, name, email, password) values (2, 'Maria Silva', 'maria@silva.com', '$2a$10$l4TKw2RXr1Put36T3qkEruXObOVGhqebp4g0f1HTAZ07smXDGkCG.');

