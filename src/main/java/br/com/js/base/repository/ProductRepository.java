package br.com.js.base.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByNameIgnoreCaseContaining(String name);
	boolean existsByNameIgnoreCaseContaining(String name);
}
