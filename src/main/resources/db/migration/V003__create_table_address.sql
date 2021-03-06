CREATE TABLE os_address (
	id SERIAL PRIMARY KEY,
	cep VARCHAR(9) NOT NULL,
	street VARCHAR(100) NOT NULL,
	neighborhood VARCHAR(50) NOT NULL,
	city VARCHAR(50) NOT NULL,
	state VARCHAR(2) NOT NULL,
	id_client BIGINT,
	FOREIGN KEY (id_client) REFERENCES os_client(id)
);
