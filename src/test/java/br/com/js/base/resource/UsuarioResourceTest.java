package br.com.js.base.resource;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.js.base.dto.UsuarioDTO;
import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Usuario;
import br.com.js.base.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioResourceTest {

	private final String URL_API = "/usuarios";

	// Simula as requisições http
	@Autowired
	MockMvc mvc;

	@MockBean
	UsuarioService service;

	private String obtainAccessToken() throws Exception {
		return obtainAccessToken("admin@admin.com", "senhas");
	}

	private String obtainAccessToken(String username, String password) throws Exception {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client", "angular");
		params.add("username", username);
		params.add("password", password);

		ResultActions result = mvc
				.perform(post("/oauth/token").params(params).with(httpBasic("angular", "@ngul@r0"))
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}

	@Test
	@DisplayName("Deve listar todos os usuários")
	public void deve_listar_todos_os_usuarios() throws Exception {
		String accessToken = obtainAccessToken();

		// @formatter:off
		MockHttpServletRequestBuilder request = 
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
		var dto = novoUsuarioDTO();

		BDDMockito.given(service.save(Mockito.any(Usuario.class)))
				.willThrow(new BusinessException("Dados Incompletos"));

		String json = toJson(dto);

		String accessToken = obtainAccessToken();

		// @formatter:off
		MockHttpServletRequestBuilder request = 
			MockMvcRequestBuilders
			.post(URL_API)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);

		mvc
			.perform(request)
			.andExpect(status().isBadRequest())			;

		// @formatter:on
	}

	@Test
	@DisplayName("Deve criar um novo usuário")
	public void deve_criar_um_novo_usuario() throws Exception {
		String accessToken = obtainAccessToken();

		var usuario = novoUsuario();
		usuario.setId(10l);
		var dto = novoUsuarioDTO();

		BDDMockito.given(service.save(Mockito.any(Usuario.class))).willReturn(usuario);

		String json = toJson(dto);

		// @formatter:off
		MockHttpServletRequestBuilder request = 
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

	private String toJson(UsuarioDTO dto) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		String json = objectMapper.writeValueAsString(dto);
		return json;
	}
	
	private UsuarioDTO novoUsuarioDTO() {
		// @formatter:off
		var dto = UsuarioDTO.builder()
					.nome("Jayme Sanches")
					.email("jayme@email.com")
					.senha("55554433")
					.build();
		return dto;
		// @formatter:on
	}

	private Usuario novoUsuario() {
		// @formatter:off
		var usuario = Usuario.builder()
				.nome("Jayme Sanches")
				.email("jayme@email.com")
				.senha("55554433")
				.build();
		return usuario;
		// @formatter:on
	}
}
