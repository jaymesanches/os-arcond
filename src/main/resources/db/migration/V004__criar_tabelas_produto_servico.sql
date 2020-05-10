CREATE TABLE servico (
	id SERIAL PRIMARY KEY,
	codigo VARCHAR(10) NOT NULL,
	descricao VARCHAR(255) NOT NULL,
	preco DECIMAL(10,2) NOT NULL,
	desconto DECIMAL(10,2)
);

CREATE TABLE produto (
	id SERIAL PRIMARY KEY,
	codigo VARCHAR(10) NOT NULL,
	descricao VARCHAR(255) NOT NULL,
	preco_custo DECIMAL(10,2),
	preco_venda DECIMAL(10,2),
	estoque NUMERIC(5) DEFAULT 0
);

