package br.com.js.base.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.js.base.dto.UsuarioDTO;
import br.com.js.base.helper.UsuarioTestHelper;
import br.com.js.base.model.Usuario;
import br.com.js.base.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioResourceTest extends BaseResourceTest {

  private final String URL_API = "/usuarios";

  // Simula as requisições http
  @Autowired
  MockMvc mvc;

  @MockBean
  UsuarioService service;

  private String accessToken;

  @BeforeEach
  public void setup() throws Exception {
    accessToken = obtainAccessToken("admin@admin.com", "senhas");
  }

  @Test
  @DisplayName("Deve listar todos os usuarios")
  public void deve_listar_todos_os_usuarios() throws Exception {
    // @formatter:off
		var request = 
			MockMvcRequestBuilders
			.get(URL_API)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			;

		mvc
			.perform(request)
			.andExpect(status().isOk());
		// @formatter:on
  }

  @Test
  @DisplayName("Deve retornar erro ao criar um usuário sem nome")
  public void deve_retornar_erro_ao_criar_usuario_sem_nome() throws Exception {
    // @formatter:off
		var dto = UsuarioTestHelper.getUsuarioDTO();
		dto.setNome(null);

		var json = toJson(dto);

		var request = 
			MockMvcRequestBuilders
			.post(URL_API)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);

		var result = mvc
			.perform(request)
			.andExpect(status().isBadRequest())			
			.andReturn().getResolvedException()
			;
		
		assertThat(result).isInstanceOf(MethodArgumentNotValidException.class);
		
		// @formatter:on
  }

  @Test
  @DisplayName("Deve criar um novo produto")
  public void deve_criar_um_novo_produto() throws Exception {
    var usuario = UsuarioTestHelper.getUsuario(1l);
    var dto = UsuarioTestHelper.getUsuarioDTO();

    given(service.save(any(Usuario.class))).willReturn(usuario);

    var json = toJson(dto);

    // @formatter:off
		var request = 
			MockMvcRequestBuilders
			.post(URL_API)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);

		mvc
			.perform(request)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").isNotEmpty())
			.andExpect(jsonPath("nome").value(dto.getNome()));

		// @formatter:on
  }

  @Test
  @DisplayName("Deve deletar um usuário existente")
  public void deve_deletar_um_usuario() throws Exception {
    // Cenário
    var id = 123l;
    given(service.findById(anyLong())).willReturn(Usuario.builder().id(id).build());

    // Execução
    // @formatter:off
		var request = 
			MockMvcRequestBuilders
			.delete(URL_API + "/{id}", "1")
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);

		mvc
			.perform(request)
			.andExpect(status().isNoContent());

		// @formatter:on
  }

  @Test
  @DisplayName("Deve retornar not found ao deletar um usuário inexistente")
  public void deve_retornar_not_found_ao_deletar_um_usuario_inexistente() throws Exception {
    // @formatter:off
		// Cenário
		doThrow(new ResourceNotFoundException()).when(service).delete(anyLong());

		// Execução
		var request = 
			MockMvcRequestBuilders
				.delete(URL_API + "/{id}", 1l)
				.header("Authorization", "Bearer " + accessToken);

		// Verificação
		mvc.perform(request).andExpect(status().isNotFound());
		// @formatter:on
  }

  @Test
  @DisplayName("Deve retornar uma lista de usuarios por nome")
  public void deve_retornar_uma_lista_de_usuarios_por_nome() throws Exception {
    // @formatter:off
		var usuario = UsuarioTestHelper.getUsuario();
		var lista = new ArrayList<Usuario>();
		lista.add(usuario);

		// Cenário
		given(service.findByNomeIgnoreCaseContaining(anyString())).willReturn(lista);

		// Execução
		var request = MockMvcRequestBuilders
				.get(URL_API + "?nome=teste")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$..nome").exists());
		// @formatter:on
  }

  @Test
  @DisplayName("Deve alterar um usuário")
  public void deve_alterar_um_usuario() throws Exception {
    // Cenário
    var usuario = UsuarioTestHelper.getUsuario(1l);
    usuario.setNome("Nome Alterado");
    given(service.update(any(Usuario.class))).willReturn(usuario);

    var dto = UsuarioTestHelper.getUsuarioDTO(1l);

    var json = toJson(dto);

    // Execução

    // @formatter:off
		var request = 
			MockMvcRequestBuilders
			.put(URL_API)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);

		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(dto.getId()))
			.andExpect(jsonPath("nome").value("Nome Alterado"));

		// @formatter:on
  }

  @Test
  @DisplayName("Deve buscar um usuário pelo código")
  public void deve_buscar_um_usuario_pelo_codigo() throws Exception {
    var usuario = UsuarioTestHelper.getUsuario(1l);
    given(service.findById(anyLong())).willReturn(usuario);

    // @formatter:off
    var request = 
    	MockMvcRequestBuilders
    	.get(URL_API + "/{id}", 1l)
    	.header("Authorization", "Bearer " + accessToken)
    	.contentType(MediaType.APPLICATION_JSON)
    	.accept(MediaType.APPLICATION_JSON);
    
    mvc
    	.perform(request)
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("nome").value(usuario.getNome()));
    // @formatter:on
  }

  @Test
  @DisplayName("Deve retornar not found ao buscar um usuário pelo código inválido")
  public void deve_retornar_not_found_ao_buscar_um_usuario_pelo_codigo_invalido() throws Exception {
    // @formatter:off
    given(service.findById(anyLong())).willReturn(null);
    
    var request = 
      MockMvcRequestBuilders
      .get(URL_API + "/{id}", 1l)
      .header("Authorization", "Bearer " + accessToken)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON);

    mvc
      .perform(request)
      .andExpect(status().isNotFound());

    // @formatter:on
  }

  private String toJson(UsuarioDTO dto) throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    var json = objectMapper.writeValueAsString(dto);
    return json;
  }
}
