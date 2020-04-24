CREATE TABLE usuario (
	id SERIAL PRIMARY KEY,
	nome VARCHAR(50) NOT NULL,
	email VARCHAR(50) NOT NULL,
	senha VARCHAR(150) NOT NULL
);

CREATE TABLE permissao (
	id SERIAL PRIMARY KEY,
	descricao VARCHAR(50) NOT NULL
);

CREATE TABLE usuario_permissao (
	id_usuario INTEGER NOT NULL,
	id_permissao INTEGER NOT NULL,
	PRIMARY KEY (id_usuario, id_permissao),
	FOREIGN KEY (id_usuario) REFERENCES usuario(id),
	FOREIGN KEY (id_permissao) REFERENCES permissao(id)
);

INSERT INTO usuario (id, nome, email, senha) values (1, 'Administrador', 'admin@admin.com', '$2a$10$l4TKw2RXr1Put36T3qkEruXObOVGhqebp4g0f1HTAZ07smXDGkCG.');
INSERT INTO usuario (id, nome, email, senha) values (2, 'Maria Silva', 'maria@silva.com', '$2a$10$l4TKw2RXr1Put36T3qkEruXObOVGhqebp4g0f1HTAZ07smXDGkCG.');

