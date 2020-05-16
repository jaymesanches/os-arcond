package br.com.js.base.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.js.base.helper.ProdutoTestHelper;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ProdutoRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	ProdutoRepository repository;

	@Test
	@DisplayName("Deve salvar um produto")
	public void deve_salvar_um_produto() throws Exception {
		var produtoSalvo = repository.save(ProdutoTestHelper.getProduto());
		assertThat(produtoSalvo.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao salvar um produto com dados incompletos")
	public void deve_retoanar_erro_ao_salvar_um_produto_com_dados_incompletos() throws Exception {
		var servico = ProdutoTestHelper.getProduto();
		servico.setDescricao(null);
		
		var excecao = Assertions.catchThrowable(() -> repository.save(servico));
		
		assertThat(excecao).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve deletar um produto")
	public void deve_deletar_um_produto() throws Exception {
		var servico = entityManager.persist(ProdutoTestHelper.getProduto());
		var excecao = Assertions.catchThrowable(() -> repository.deleteById(servico.getId()));
		assertThat(excecao).isNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao deletar um produto inexistente")
	public void deve_retornar_erro_ao_deletar_um_produto_inexistente() throws Exception {
		var idInexistente = 987654321l;
		var excecao = Assertions.catchThrowable(() -> repository.deleteById(idInexistente));
		assertThat(excecao).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	@DisplayName("Deve retornar true se existir produto com a descrição informada")
	public void deve_retornar_true_se_existir_produto_com_a_descricao_informada() {
		// Cenário
		var produto = ProdutoTestHelper.getProduto();
		var produtoSalvo = entityManager.persist(produto);

		// Execução
		boolean exists = repository.existsByDescricaoIgnoreCaseContaining("Filtro");

		// Verificação
		assertThat(exists).isTrue();
		assertThat(produtoSalvo.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar nulo se não existir produto com a descrição informada")
	public void deve_retornar_null_se_nao_existir_cliente_com_a_descricao_informada() {
		// Execução
		boolean exists = repository.existsByDescricaoIgnoreCaseContaining("Filtro");

		// Verificação
		assertThat(exists).isFalse();
	}
}
