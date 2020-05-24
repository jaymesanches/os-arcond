CREATE TABLE os_order (
	id SERIAL PRIMARY KEY,
	number NUMERIC(4) NOT NULL,
	year NUMERIC(4) NOT NULL,
	price DECIMAL(10,2) NOT NULL,
	discount DECIMAL(10,2),
	date_in TIMESTAMP NOT NULL,
	data_out TIMESTAMP,
	date_end TIMESTAMP,
	status VARCHAR(20) NOT NULL,
	id_client BIGINT NOT NULL,
	
	FOREIGN KEY (id_client) REFERENCES os_client(id)
);

CREATE TABLE os_order_work (
	id_order BIGINT NOT NULL,
	id_work BIGINT NOT NULL,
	PRIMARY KEY (id_order, id_work),
	FOREIGN KEY (id_order) REFERENCES os_order(id),
	FOREIGN KEY (id_work) REFERENCES os_work(id)
);

CREATE TABLE os_order_product (
	id_order BIGINT NOT NULL,
	id_product BIGINT NOT NULL,
	PRIMARY KEY (id_order, id_product),
	FOREIGN KEY (id_order) REFERENCES os_order(id),
	FOREIGN KEY (id_product) REFERENCES os_product(id)
);