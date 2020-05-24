CREATE TABLE os_client (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	email VARCHAR(100),
	document VARCHAR(20),
	birth_date DATE,
	phone VARCHAR(20) NOT NULL,
	created_at TIMESTAMP
);
