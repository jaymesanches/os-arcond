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

import br.com.js.base.helper.ServicoTestHelper;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ServicoRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	ServicoRepository repository;

	@Test
	@DisplayName("Deve salvar um serviço")
	public void deve_salvar_um_servico() throws Exception {
		var servicoSalvo = repository.save(ServicoTestHelper.getServico());
		assertThat(servicoSalvo.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao salvar um serviço com dados incompletos")
	public void deve_retoanar_erro_ao_salvar_um_servico_com_dados_incompletos() throws Exception {
		var servico = ServicoTestHelper.getServico();
		servico.setDescricao(null);
		
		var excecao = Assertions.catchThrowable(() -> repository.save(servico));
		
		assertThat(excecao).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve deletar um serviço")
	public void deve_deletar_um_servico() throws Exception {
		var servico = entityManager.persist(ServicoTestHelper.getServico());
		var excecao = Assertions.catchThrowable(() -> repository.deleteById(servico.getId()));
		assertThat(excecao).isNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao deletar um serviço inexistente")
	public void deve_retornar_erro_ao_deletar_um_servico_inexistente() throws Exception {
		var idInexistente = 987654321l;
		var excecao = Assertions.catchThrowable(() -> repository.deleteById(idInexistente));
		assertThat(excecao).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	@DisplayName("Deve retornar true se existir serviço com a descrição informada")
	public void deve_retornar_true_se_existir_servico_com_a_descricao_informada() {
		// Cenário
		var servico = ServicoTestHelper.getServico();
		var servicoSalvo = entityManager.persist(servico);

		// Execução
		boolean exists = repository.existsByDescricaoIgnoreCaseContaining("Filtro");

		// Verificação
		assertThat(exists).isTrue();
		assertThat(servicoSalvo.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar nulo se não existir serviço com a descrição informada")
	public void deve_retornar_null_se_nao_existir_cliente_com_a_descricao_informada() {
		// Cenário
		// Execução
		boolean exists = repository.existsByDescricaoIgnoreCaseContaining("Filtro");

		// Verificação
		assertThat(exists).isFalse();
	}
}
