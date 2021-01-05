CREATE TABLE os_order_item (
	id SERIAL PRIMARY KEY,
	sequencial NUMERIC(4) NOT NULL,
	amount NUMERIC(5) NOT NULL,
	price DECIMAL(10,2) NOT NULL,
	id_order BIGINT NOT NULL,
	id_product BIGINT,
	id_work BIGINT,
	FOREIGN KEY (id_order) REFERENCES os_order(id),
	FOREIGN KEY (id_product) REFERENCES os_product(id),
	FOREIGN KEY (id_work) REFERENCES os_work(id)
);


