package br.com.js.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
	
}
