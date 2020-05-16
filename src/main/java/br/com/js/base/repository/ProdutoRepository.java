package br.com.js.base.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
	List<Produto> findByDescricaoIgnoreCaseContaining(String descricao);
	boolean existsByDescricaoIgnoreCaseContaining(String descricao);
}
