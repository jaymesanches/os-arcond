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
import br.com.js.base.helper.UsuarioTestHelper;
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
	@DisplayName("Deve pesquisar todos os usuários")
	public void deve_pesquisar_todos_os_usuarios() throws Exception {
		// Cenário

		var usuarios = UsuarioTestHelper.obterListaComDoisUsuarios();

		when(repository.findAll()).thenReturn(usuarios);

		// Execução
		var lista = service.findAll();

		// Verificação
		assertThat(lista).isNotEmpty();
		assertThat(lista.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Deve retornar um usuário pelo id")
	public void deve_retornar_um_usuario_pelo_id() throws Exception {
		var usuarioOptional = Optional.of(UsuarioTestHelper.getUsuario());
		when(repository.findById(anyLong())).thenReturn((usuarioOptional));

		var usuario = service.findById(1l);
		assertThat(usuario).isNotNull();
	}

	@Test
	@DisplayName("Deve retornar nulo quando pesquisar por id inválido")
	public void deve_retornar_erro_quando_pesquisado_por_id_inválido() throws Exception {
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		var usuario = service.findById(1l);
		assertThat(usuario).isNull();
	}

	@Test
	@DisplayName("Deve retornar uma lista de usuários ao pesquisa por parte do nome")
	public void deve_pesquisar_uma_lista_de_usuarios_ao_pesquisar_por_parte_do_nome() throws Exception {
		var usuarios = UsuarioTestHelper.obterListaComDoisUsuarios();
		when(repository.findByNomeIgnoreCaseContaining(anyString())).thenReturn(usuarios);

		var lista = service.findByNomeIgnoreCaseContaining("jayme");
		assertThat(lista.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Deve salvar um usuário")
	public void deve_salvar_um_usuario() {
		// @formatter:off

		// Cenário
		var usuario = UsuarioTestHelper.getUsuario();

		when(repository.save(usuario))
			.thenReturn(UsuarioTestHelper.getUsuario(1l));
		// Execução
		var usuarioSalvo = service.save(usuario);

		// Verificação
		assertThat(usuarioSalvo.getId()).isNotNull();
		assertThat(usuarioSalvo.getNome()).isEqualTo("Jayme");
		
		// @formatter:on
	}

	@Test
	@DisplayName("Deve remover um usuário")
	public void deve_remover_um_usuario() throws Exception {
		when(repository.existsById(anyLong())).thenReturn(true);
		service.delete(123l);
		verify(repository, atLeastOnce()).deleteById(anyLong());
	}

	@Test
	@DisplayName("Deve retornar exceção ao tentar remover um usuário com id inválido")
	public void deve_retornar_erro_ao_remover_um_usuario_por_id_invalido() throws Exception {
		doNothing().when(repository).deleteById(anyLong());

		Throwable exception = Assertions.catchThrowable(() -> service.delete(1l));

		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("mensagem",
				"Usuário inexistente");
	}

	@Test
	@DisplayName("Deve retornar erro ao criar um usuário com dados incompletos")
	public void deve_retornar_erro_ao_criar_usuario_com_dados_incompletos() {
		// Cenário
		var usuario = UsuarioTestHelper.getUsuario();
		usuario.setNome(null);

		when(repository.save(usuario)).thenThrow(DataIntegrityViolationException.class);

		// Execução
		Throwable exception = Assertions.catchThrowable(() -> service.save(usuario));

		// Verificação
		assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve alterar um usuário")
	public void deve_alterar_um_usuario() throws Exception {
		var usuario = UsuarioTestHelper.getUsuario(1l);
		usuario.setNome("Nome alterado");
		var optional = Optional.of(usuario);
		when(repository.findById(1l)).thenReturn(optional);
		when(repository.save(usuario)).thenReturn(usuario);
		
		var usuarioAlterado = service.update(usuario);
		
		assertThat(usuarioAlterado.getNome().equals("Nome alterado"));
		verify(repository, Mockito.atLeastOnce()).save(usuario);
	}
	
	@Test
	@DisplayName("Deve retornar erro ao alterar um usuário não existente")
	public void deve_retornar_erro_ao_tentar_alterar_um_usuario_nao_existente() throws Exception {
		var usuario = UsuarioTestHelper.getUsuario();
		when(repository.findById(anyLong())).thenThrow(new ResourceNotFoundException("Usuário não existe"));
		
		Throwable exception = Assertions.catchThrowable(() -> service.update(usuario));
		
		assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
		verify(repository, never()).save(usuario);
	}
}
