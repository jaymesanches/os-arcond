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

import br.com.js.base.helper.UsuarioTestHelper;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UsuarioRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	UsuarioRepository repository;

	@Test
	@DisplayName("Deve salvar um usuário")
	public void deve_salvar_um_usuario() throws Exception {
		var usuarioSalvo = repository.save(UsuarioTestHelper.getUsuario());
		assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao salvar um usuário com dados incompletos")
	public void deve_retoanar_erro_ao_salvar_um_usuario_com_dados_incompletos() throws Exception {
		var usuario = UsuarioTestHelper.getUsuario();
		usuario.setNome(null);
		
		var excecao = Assertions.catchThrowable(() -> repository.save(usuario));
		
		assertThat(excecao).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve deletar um usuário")
	public void deve_deletar_um_usuario() throws Exception {
		var usuario = entityManager.persist(UsuarioTestHelper.getUsuario());
		var excecao = Assertions.catchThrowable(() -> repository.deleteById(usuario.getId()));
		assertThat(excecao).isNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao deletar um usuário inexistente")
	public void deve_retornar_erro_ao_deletar_um_usuario_inexistente() throws Exception {
		var idInexistente = 987654321l;
		var excecao = Assertions.catchThrowable(() -> repository.deleteById(idInexistente));
		assertThat(excecao).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	@DisplayName("Deve retornar true se existir usuário com o nome informado")
	public void deve_retornar_true_se_existir_usuario_com_o_nome_informado() {
		// Cenário
		var usuario = UsuarioTestHelper.getUsuario();
		var usuarioSalvo = entityManager.persist(usuario);

		// Execução
		boolean exists = repository.existsByNomeIgnoreCaseContaining("Jayme");

		// Verificação
		assertThat(exists).isTrue();
		assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar nulo se não existir usuário com o nome informado")
	public void deve_retornar_null_se_nao_existir_usuario_com_o_nome_informado() {
		// Cenário
		// Execução
		boolean exists = repository.existsByNomeIgnoreCaseContaining("Jayme");

		// Verificação
		assertThat(exists).isFalse();
	}
}
