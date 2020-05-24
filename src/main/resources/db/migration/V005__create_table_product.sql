CREATE TABLE os_product (
	id SERIAL PRIMARY KEY,
	sku VARCHAR(10) NOT NULL,
	name VARCHAR(255) NOT NULL,
	cost_price DECIMAL(10,2),
	sale_price DECIMAL(10,2),
	stock NUMERIC(5) DEFAULT 0
);

