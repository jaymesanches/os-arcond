CREATE TABLE ordem_servico (
	id SERIAL PRIMARY KEY,
	numero NUMERIC(4) NOT NULL,
	ano NUMERIC(4) NOT NULL,
	preco DECIMAL(10,2) NOT NULL,
	desconto DECIMAL(10,2),
	data_entrada TIMESTAMP NOT NULL,
	data_entrega TIMESTAMP,
	data_finalizacao TIMESTAMP,
	status VARCHAR(20) NOT NULL,
	id_cliente BIGINT NOT NULL,
	
	FOREIGN KEY (id_cliente) REFERENCES cliente(id)
);

CREATE TABLE ordem_servico_servico (
	id_ordem_servico BIGINT NOT NULL,
	id_servico BIGINT NOT NULL,
	PRIMARY KEY (id_ordem_servico, id_servico),
	FOREIGN KEY (id_ordem_servico) REFERENCES ordem_servico(id),
	FOREIGN KEY (id_servico) REFERENCES servico(id)
);

CREATE TABLE ordem_servico_produto (
	id_ordem_servico BIGINT NOT NULL,
	id_produto BIGINT NOT NULL,
	PRIMARY KEY (id_ordem_servico, id_produto),
	FOREIGN KEY (id_ordem_servico) REFERENCES ordem_servico(id),
	FOREIGN KEY (id_produto) REFERENCES produto(id)
);