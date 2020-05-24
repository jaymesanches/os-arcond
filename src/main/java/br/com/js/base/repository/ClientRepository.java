package br.com.js.base.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
	public List<Client> findByNameIgnoreCaseContaining(String name);
	public boolean existsByNameIgnoreCaseContaining(String name);
}
