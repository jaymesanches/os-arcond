package br.com.js.base.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
	Optional<Produto> findByDescricaoIgnoringCaseContaining(String descricao);
}
