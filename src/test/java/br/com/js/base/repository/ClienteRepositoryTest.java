package br.com.js.base.repository;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.js.base.helper.ClienteTestHelper;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ClienteRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	ClienteRepository repository;

	@Test
	@DisplayName("Deve salvar um cliente")
	public void deve_salvar_um_cliente() throws Exception {
		var clienteSalvo = repository.save(ClienteTestHelper.getCliente());
		assertThat(clienteSalvo.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao salvar um cliente com dados incompletos")
	public void deve_retoanar_erro_ao_salvar_um_cliente_com_dados_incompletos() throws Exception {
		var cliente = ClienteTestHelper.getCliente();
		cliente.setNome(null);
		
		var excecao = Assertions.catchThrowable(() -> repository.save(cliente));
		
		assertThat(excecao).isInstanceOf(ConstraintViolationException.class);
	}
	
	@Test
	@DisplayName("Deve deletar um cliente")
	public void deve_deletar_um_cliente() throws Exception {
		var cliente = entityManager.persist(ClienteTestHelper.getCliente());
		var excecao = Assertions.catchThrowable(() -> repository.deleteById(cliente.getId()));
		assertThat(excecao).isNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao deletar um cliente inexistente")
	public void deve_retornar_erro_ao_deletar_um_cliente_inexistente() throws Exception {
		var idInexistente = 987654321l;
		var excecao = Assertions.catchThrowable(() -> repository.deleteById(idInexistente));
		assertThat(excecao).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	@DisplayName("Deve retornar true se existir cliente com o nome informado")
	public void deve_retornar_true_se_existir_cliente_com_o_nome_informado() {
		// Cenário
		var cliente = ClienteTestHelper.getCliente();
		var clienteSalvo = entityManager.persist(cliente);

		// Execução
		boolean exists = repository.existsByNomeIgnoreCaseContaining("Jayme");

		// Verificação
		assertThat(exists).isTrue();
		assertThat(clienteSalvo.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar nulo se não existir cliente com o nome informado")
	public void deve_retornar_null_se_nao_existir_cliente_com_o_nome_informado() {
		// Cenário
		// Execução
		boolean exists = repository.existsByNomeIgnoreCaseContaining("Jayme");

		// Verificação
		assertThat(exists).isFalse();
	}
}
