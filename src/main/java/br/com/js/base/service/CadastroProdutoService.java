package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Produto;
import br.com.js.base.repository.ProdutoRepository;

@Service
public class CadastroProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;

	public List<Produto> findAll() {
		return produtoRepository.findAll();
	}
	
	public Optional<Produto> findById(Long id) {
		return produtoRepository.findById(id);
	}
	
	public Optional<Produto> findByDescricao(String descricao) {
		if(descricao == null) {
			throw new BusinessException("Descrição precisa ser preenchido");
		}
		return produtoRepository.findByDescricaoIgnoringCaseContaining(descricao);
	}
	
	public Produto save(Produto produto) {
		return produtoRepository.save(produto);
	}

	public void delete(Long id) {
		produtoRepository.deleteById(id);
	}
}
