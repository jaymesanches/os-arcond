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

import br.com.js.base.helper.UserTestHelper;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	UserRepository repository;

	@Test
	@DisplayName("Deve salvar um usuário")
	public void Should_SaveUser() throws Exception {
		var savedUser = repository.save(UserTestHelper.getUser());
		assertThat(savedUser.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao salvar um usuário com dados incompletos")
	public void Should_ThrowException_When_SaveInvalidUser() throws Exception {
		var user = UserTestHelper.getUser();
		user.setName(null);
		
		var exception = Assertions.catchThrowable(() -> repository.save(user));
		
		assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve deletar um usuário")
	public void Should_DeleteUser() throws Exception {
		var user = entityManager.persist(UserTestHelper.getUser());
		
		var exception = Assertions.catchThrowable(() -> repository.deleteById(user.getId()));
		
		assertThat(exception).isNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao deletar um usuário inexistente")
	public void Should_ThrowException_When_DeleteInvalidUser() throws Exception {
		var invalidID = 987654321l;
		
		var exception = Assertions.catchThrowable(() -> repository.deleteById(invalidID));
		
		assertThat(exception).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	@DisplayName("Deve retornar true se existir usuário com o nome informado")
	public void Should_ReturnTrue_IfExists_UserWithName() {
		var user = UserTestHelper.getUser();
		var savedUser = entityManager.persist(user);

		boolean exists = repository.existsByNameIgnoreCaseContaining("Jayme");

		assertThat(exists).isTrue();
		assertThat(savedUser.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar nulo se não existir usuário com o nome informado")
	public void Should_ReturnNull_IfNotExists_UserWithName() {
		boolean exists = repository.existsByNameIgnoreCaseContaining("Jayme");

		assertThat(exists).isFalse();
	}
}
