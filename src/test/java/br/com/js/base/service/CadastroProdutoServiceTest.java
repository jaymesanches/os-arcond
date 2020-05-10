package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Produto;
import br.com.js.base.repository.ProdutoRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CadastroProdutoServiceTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	CadastroProdutoService service;

	@MockBean
	ProdutoRepository repository;

	@Test
	@DisplayName("Deve salvar um produto")
	public void deve_salvar_um_produto() {
		// Cenário
		var produto = novoProduto();

		// @formatter:off
		Mockito.when(repository.save(produto))
			.thenReturn(Produto.builder()
				.id(10l)
				.descricao("Filtro de ar")
				.codigo("100")
				.estoque(10)
				.precoCusto(BigDecimal.ONE)
				.precoVenda(BigDecimal.TEN)
				.build());
		// @formatter:on

		// Execução
		var produtoSalvo = service.save(produto);

		// Verificação
		assertThat(produtoSalvo.getId()).isNotNull();
		assertThat(produtoSalvo.getDescricao()).isEqualTo("Filtro de ar");
	}

//	@Test
//	@DisplayName("Deve retornar erro ao criar um produto com dados incompletos")
//	public void deve_retornar_erro_ao_criar_produto_com_dados_incompletos() {
//		// Cenário
//		var produto = novoProduto();
//
//		Mockito.when(repository.save(Mockito.any(Produto.class)))
//				.thenThrow(new BusinessException("Dados insuficientes"));
//
//		// Execução
//		Throwable exception = Assertions.catchThrowable(() -> service.save(produto));
//
//		// Verificação
//		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("mensagem",
//				"Dados insuficientes");
//		Mockito.verify(repository, Mockito.never()).save(produto);
//	}

	@Test
	@DisplayName("Deve retornar erro ao pesquisar um produto por nome sem passar o nome")
	public void deve_retornar_erro_ao_pesquisar_cliente_pelo_nome_sem_nome() {
		// Cenário
		String descricao = null;

		// Execução
		Throwable exception = Assertions.catchThrowable(() -> service.findByDescricao(descricao));

		// Verificação
		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("mensagem",
				"Descrição precisa ser preenchido");
		Mockito.verify(repository, Mockito.never()).findByDescricaoIgnoringCaseContaining(descricao);
	}

	private Produto novoProduto() {
		// @formatter:off
		return Produto.builder()
			.descricao("Filtro de ar")
			.codigo("100")
			.estoque(10)
			.precoCusto(BigDecimal.ONE)
			.precoVenda(BigDecimal.TEN)
			.build();
		// @formatter:on
	}
}
