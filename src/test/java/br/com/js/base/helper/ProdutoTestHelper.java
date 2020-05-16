package br.com.js.base.helper;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.js.base.dto.ProdutoDTO;
import br.com.js.base.model.Produto;

public class ProdutoTestHelper {

	public static Produto getProduto() {
		return getProduto(null);
	}

	public static Produto getProduto(Long id) {
		// @formatter:off
		return Produto.builder()
			.id(id)
			.descricao("Filtro de ar")
			.codigo("100")
			.estoque(10)
			.precoCusto(BigDecimal.ONE)
			.precoVenda(BigDecimal.TEN)
			.build();
		// @formatter:on
	}

	public static ProdutoDTO getProdutoDTO() {
		return getProdutoDTO(null);
	}

	public static ProdutoDTO getProdutoDTO(Long id) {
		// @formatter:off
		return ProdutoDTO.builder()
				.id(id)
				.descricao("Filtro de ar")
				.codigo("100")
				.estoque(10)
				.precoCusto(BigDecimal.ONE)
				.precoVenda(BigDecimal.TEN)
				.build();
		// @formatter:on
	}

	public static ArrayList<Produto> obterListaComDoisProdutos() {
		// @formatter:off
		var produto1 = Produto.builder()
			.codigo("F123")
			.descricao("Filtro de ar")
			.estoque(10)
			.precoCusto(BigDecimal.ONE)
			.precoVenda(BigDecimal.TEN)
			.build();

		var produto2 = Produto.builder()
			.codigo("F321")
			.descricao("Filtro de óleo")
			.estoque(10)
			.precoCusto(BigDecimal.ONE)
			.precoVenda(BigDecimal.TEN)
			.build();
		
		var produtos = new ArrayList<Produto>();
		produtos.add(produto1);
		produtos.add(produto2);
		
		return produtos;
		// @formatter:on
	}
}
