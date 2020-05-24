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

import br.com.js.base.helper.ClientTestHelper;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ClientRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	ClientRepository repository;

	@Test
	@DisplayName("Deve salvar um cliente")
	public void Should_SaveClient() throws Exception {
		var savedClient = repository.save(ClientTestHelper.getClient());
		assertThat(savedClient.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao salvar um cliente com dados incompletos")
	public void Should_ThrowException_When_SaveWithInvalidData() throws Exception {
		var client = ClientTestHelper.getClient();
		client.setName(null);
		
		var exception = Assertions.catchThrowable(() -> repository.save(client));
		
		assertThat(exception).isInstanceOf(ConstraintViolationException.class);
	}
	
	@Test
	@DisplayName("Deve deletar um cliente")
	public void Should_DeleteClient() throws Exception {
		var client = entityManager.persist(ClientTestHelper.getClient());
		var exception = Assertions.catchThrowable(() -> repository.deleteById(client.getId()));
		assertThat(exception).isNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao deletar um cliente inexistente")
	public void Should_ThrowException_When_DeleteInvalidClient() throws Exception {
		var invalidId = 987654321l;
		var exception = Assertions.catchThrowable(() -> repository.deleteById(invalidId));
		assertThat(exception).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	@DisplayName("Deve retornar true se existir cliente com o nome informado")
	public void Should_ReturnTrue_IfExists_ClientWithName() {
		var client = ClientTestHelper.getClient();
		var savedClient = entityManager.persist(client);

		boolean exists = repository.existsByNameIgnoreCaseContaining("Jayme");

		assertThat(exists).isTrue();
		assertThat(savedClient.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar nulo se não existir cliente com o nome informado")
	public void Should_ReturnNull_IfNotExists_ClientWithName() {
		boolean exists = repository.existsByNameIgnoreCaseContaining("Jayme");

		assertThat(exists).isFalse();
	}
}
