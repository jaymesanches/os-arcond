package br.com.js.base.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Produto;
import br.com.js.base.repository.ProdutoRepository;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;

	public List<Produto> findAll() {
		return produtoRepository.findAll();
	}

	public Produto findById(Long id) {
		var optional = produtoRepository.findById(id);
		return optional.orElse(null);
	}

	public List<Produto> findByDescricaoIgnoreCaseContaining(String descricao) {
		if (descricao == null) {
			throw new BusinessException("Descrição precisa ser preenchido");
		}

		return produtoRepository.findByDescricaoIgnoreCaseContaining(descricao);
	}

	public Produto save(Produto produto) {
		return produtoRepository.save(produto);
	}

	public void delete(Long id) {
		if (!produtoRepository.existsById(id)) {
			throw new BusinessException("Produto inexistente");
		}
		
		produtoRepository.deleteById(id);
	}

	public Produto update(Produto produto) {
		var produtoOptional = produtoRepository.findById(produto.getId());
		var produtoSalvo = produtoOptional.orElseThrow(() -> new ResourceNotFoundException("Produto não existe"));
		BeanUtils.copyProperties(produto, produtoSalvo);
		return produtoRepository.save(produtoSalvo);
	}
}
