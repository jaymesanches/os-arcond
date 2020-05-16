package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.helper.ProdutoTestHelper;
import br.com.js.base.model.Produto;
import br.com.js.base.repository.ProdutoRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProdutoServiceTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ProdutoService service;

	@MockBean
	ProdutoRepository repository;

	@Test
	@DisplayName("Deve pesquisar todos os produtos")
	public void deve_pesquisar_todos_os_produtos() throws Exception {
		// Cenário

		var produtos = ProdutoTestHelper.obterListaComDoisProdutos();

		when(repository.findAll()).thenReturn(produtos);

		// Execução
		var lista = service.findAll();

		// Verificação
		assertThat(lista).isNotEmpty();
		assertThat(lista.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Deve retornar um produto pelo id")
	public void deve_retornar_um_produto_pelo_id() throws Exception {
		var produtoOptional = Optional.of(ProdutoTestHelper.getProduto());
		when(repository.findById(anyLong())).thenReturn((produtoOptional));

		var produto = service.findById(1l);
		assertThat(produto).isNotNull();
	}

	@Test
	@DisplayName("Deve retornar nulo quando pesquisar por id inválido")
	public void deve_retornar_erro_quando_pesquisado_por_id_inválido() throws Exception {
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		var produto = service.findById(1l);
		assertThat(produto).isNull();
	}

	@Test
	@DisplayName("Deve retornar uma lista de produtos ao pesquisa por parte da descrição")
	public void deve_pesquisar_uma_lista_de_produtos_ao_pesquisar_por_parte_da_descricao() throws Exception {
		var produtos = ProdutoTestHelper.obterListaComDoisProdutos();
		when(repository.findByDescricaoIgnoreCaseContaining(anyString())).thenReturn(produtos);

		var lista = service.findByDescricaoIgnoreCaseContaining("filtro");
		assertThat(lista.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Deve salvar um produto")
	public void deve_salvar_um_produto() {
		// @formatter:off

		// Cenário
		var produto = ProdutoTestHelper.getProduto();

		when(repository.save(produto))
			.thenReturn(ProdutoTestHelper.getProduto(1l));

		// Execução
		var produtoSalvo = service.save(produto);

		// Verificação
		assertThat(produtoSalvo.getId()).isNotNull();
		assertThat(produtoSalvo.getDescricao()).asString().contains("Filtro");
		
		// @formatter:on
	}

	@Test
	@DisplayName("Deve remover um produto")
	public void deve_remover_um_produto() throws Exception {
		when(repository.existsById(anyLong())).thenReturn(true);
		service.delete(123l);
		verify(repository, atLeastOnce()).deleteById(anyLong());
	}

	@Test
	@DisplayName("Deve retornar exceção ao tentar remover um produto com id inválido")
	public void deve_retornar_erro_ao_remover_um_produto_por_id_invalido() throws Exception {
		doNothing().when(repository).deleteById(anyLong());
		Throwable exception = Assertions.catchThrowable(() -> service.delete(1l));

		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("mensagem",
				"Produto inexistente");
	}

	@Test
	@DisplayName("Deve retornar erro ao criar um produto com dados incompletos")
	public void deve_retornar_erro_ao_criar_produto_com_dados_incompletos() {
		// Cenário
		var produto = ProdutoTestHelper.getProduto();
		produto.setDescricao(null);

		when(repository.save(produto)).thenThrow(DataIntegrityViolationException.class);

		// Execução
		Throwable exception = Assertions.catchThrowable(() -> service.save(produto));

		// Verificação
		assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve alterar um produto")
	public void deve_alterar_um_produto() throws Exception {
		var produto = ProdutoTestHelper.getProduto();
		produto.setId(1l);
		var optional = Optional.of(produto);
		when(repository.findById(1l)).thenReturn(optional);
		when(repository.save(Mockito.any(Produto.class))).thenReturn(produto);
		
		var produtoAlterado = service.update(produto);
		
		assertThat(produtoAlterado.getDescricao().equals(produto.getDescricao()));
		verify(repository, Mockito.atLeastOnce()).save(produto);
	}
	
	@Test
	@DisplayName("Deve retornar erro ao alterar um cliente não existente")
	public void deve_retornar_erro_ao_tentar_alterar_um_cliente_nao_existente() throws Exception {
		var produto = ProdutoTestHelper.getProduto();
		when(repository.findById(anyLong())).thenThrow(new ResourceNotFoundException("Cliente não existe"));
		
		Throwable exception = Assertions.catchThrowable(() -> service.update(produto));
		
		assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
		verify(repository, never()).save(produto);
	}
}
