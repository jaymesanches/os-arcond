package br.com.js.base.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Work;

public interface WorkRepository extends JpaRepository<Work, Long> {
	List<Work> findByNameIgnoreCaseContaining(String name);
	boolean existsByNameIgnoreCaseContaining(String name);
}
