package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;

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
import br.com.js.base.model.Usuario;
import br.com.js.base.repository.UsuarioRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioServiceTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	UsuarioService service;

	@MockBean
	UsuarioRepository repository;

	@Test
	@DisplayName("Deve salvar um usuario")
	public void deve_salvar_um_usuario() {
		// Cenário
		var usuario = novoUsuario();

		// @formatter:off
		Mockito.when(repository.save(usuario))
			.thenReturn(Usuario.builder()
				.id(10l)
				.nome("Jayme")
				.senha("")
				.email("jayme@email.com")
				.build());
		// @formatter:on

		// Execução
		var usuarioSalvo = service.save(usuario);

		// Verificação
		assertThat(usuarioSalvo.getId()).isNotNull();
		assertThat(usuarioSalvo.getNome()).isEqualTo("Jayme");
	}

	@Test
	@DisplayName("Deve retornar erro ao pesquisar um usuário por nome sem passar o nome")
	public void deve_retornar_erro_ao_pesquisar_usuario_pelo_nome_sem_nome() {
		// Cenário
		String nome = null;

		// Execução
		Throwable exception = Assertions.catchThrowable(() -> service.findByNome(nome));

		// Verificação
		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("mensagem",
				"Nome precisa ser preenchido");
		Mockito.verify(repository, Mockito.never()).findByNomeIgnoringCaseContaining(nome);
	}

	private Usuario novoUsuario() {
		// @formatter:off
		return Usuario.builder()
			.nome("Jayme")
			.senha("12345678909")
			.email("jayme@email.com")
			.build();
		// @formatter:on
	}
}
