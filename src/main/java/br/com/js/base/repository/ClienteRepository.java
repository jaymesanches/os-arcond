package br.com.js.base.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	public List<Cliente> findByNomeIgnoringCaseContaining(String nome);

}
