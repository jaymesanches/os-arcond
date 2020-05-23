CREATE TABLE endereco (
	id SERIAL PRIMARY KEY,
	cep VARCHAR(9) NOT NULL,
	logradouro VARCHAR(100) NOT NULL,
	complemento VARCHAR(100),
	bairro VARCHAR(50) NOT NULL,
	localidade VARCHAR(50) NOT NULL,
	uf VARCHAR(2) NOT NULL,
	unidade VARCHAR(50) NOT NULL,
	ibge VARCHAR(20),
	gia VARCHAR(20),
	id_cliente BIGINT NOT NULL,
	
	FOREIGN KEY (id_cliente) REFERENCES cliente(id)
);
