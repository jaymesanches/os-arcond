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

import br.com.js.base.helper.WorkTestHelper;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class WorkRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	WorkRepository repository;

	@Test
	@DisplayName("Deve salvar um serviço")
	public void Should_SaveWork() throws Exception {
		var savedWork = repository.save(WorkTestHelper.getWork());
		assertThat(savedWork.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao salvar um serviço com dados incompletos")
	public void Should_ThrowException_When_SaveInvalidWork() throws Exception {
		var work = WorkTestHelper.getWork();
		work.setName(null);
		
		var exception = Assertions.catchThrowable(() -> repository.save(work));
		
		assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve deletar um serviço")
	public void Should_DeleteUser() throws Exception {
		var work = entityManager.persist(WorkTestHelper.getWork());
		
		var exception = Assertions.catchThrowable(() -> repository.deleteById(work.getId()));
		
		assertThat(exception).isNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao deletar um serviço inexistente")
	public void Should_ThrowException_When_DeleteInvalidUser() throws Exception {
		var invalidId = 987654321l;
		var exception = Assertions.catchThrowable(() -> repository.deleteById(invalidId));
		assertThat(exception).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	@DisplayName("Deve retornar true se existir serviço com a descrição informada")
	public void Should_ReturnTrue_IfExists_WorkWithName() {
		var work = WorkTestHelper.getWork();
		var savedWork = entityManager.persist(work);

		boolean exists = repository.existsByNameIgnoreCaseContaining("Filtro");

		assertThat(exists).isTrue();
		assertThat(savedWork.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar nulo se não existir serviço com a descrição informada")
	public void Should_ReturnNull_IfNotExists_WorkWithName() {
		boolean exists = repository.existsByNameIgnoreCaseContaining("Filtro");

		assertThat(exists).isFalse();
	}
}
