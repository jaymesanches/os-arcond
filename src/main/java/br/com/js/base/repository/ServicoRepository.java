package br.com.js.base.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
	List<Servico> findByDescricaoIgnoreCaseContaining(String descricao);
	boolean existsByDescricaoIgnoreCaseContaining(String descricao);
}
