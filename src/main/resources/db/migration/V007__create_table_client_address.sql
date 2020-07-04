CREATE TABLE os_client_address (
	id_client INTEGER NOT NULL,
	id_address INTEGER NOT NULL,
	PRIMARY KEY (id_client, id_address),
	FOREIGN KEY (id_client) REFERENCES os_client(id),
	FOREIGN KEY (id_address) REFERENCES os_address(id)
);


