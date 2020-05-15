package br.com.js.base.resource;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

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
import org.springframework.dao.EmptyResultDataAccessException;
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

import br.com.js.base.dto.ProdutoDTO;
import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Produto;
import br.com.js.base.service.ProdutoService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProdutoResourceTest {

	private final String URL_API = "/produtos";

	// Simula as requisições http
	@Autowired
	MockMvc mvc;

	@MockBean
	ProdutoService service;

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
	@DisplayName("Deve listar todos os produtos")
	public void deve_listar_todos_os_produtos() throws Exception {
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
	@DisplayName("Deve retornar erro ao criar um produto sem descrição")
	public void deve_retornar_erro_ao_criar_produto_sem_descricao() throws Exception {
		var dto = novoProdutoDTO();

		BDDMockito.given(service.save(Mockito.any(Produto.class)))
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

		var usuario = novoProduto();
		usuario.setId(10l);
		var dto = novoProdutoDTO();

		BDDMockito.given(service.save(Mockito.any(Produto.class))).willReturn(usuario);

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
			.andExpect(jsonPath("descricao").value(dto.getDescricao()));

		// @formatter:on
	}

	@Test
	@DisplayName("Deve remover um produto")
	public void deve_remover_um_produto() throws Exception {
		String accessToken = obtainAccessToken();

		Mockito.doNothing().when(service).delete(Mockito.anyLong());

		// @formatter:off
		MockHttpServletRequestBuilder request = 
			MockMvcRequestBuilders
			.delete(URL_API+"/10")
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			;

		mvc
			.perform(request)
			.andExpect(status().isNoContent());

		// @formatter:on
	}
	

	@Test
	@DisplayName("Deve retornar erro ao remover um produto inexistente")
	public void deve_retornar_erro_ao_remover_produto_inexistente() throws Exception {
		String accessToken = obtainAccessToken();

		Mockito.doThrow(EmptyResultDataAccessException.class).when(service).delete(Mockito.anyLong());

		// @formatter:off
		MockHttpServletRequestBuilder request = 
			MockMvcRequestBuilders
			.delete(URL_API+"/10")
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			;

		mvc
			.perform(request)
			.andExpect(status().isNotFound());

		// @formatter:on
	}

	private String toJson(ProdutoDTO dto) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		String json = objectMapper.writeValueAsString(dto);
		return json;
	}

	private ProdutoDTO novoProdutoDTO() {
		// @formatter:off
		var dto = ProdutoDTO.builder()
			.codigo("10")
			.descricao("Filtro")
			.precoCusto(BigDecimal.ONE)
			.precoVenda(BigDecimal.TEN)
			.estoque(10)
			.build();
		return dto;
		// @formatter:on
	}

	private Produto novoProduto() {
		// @formatter:off
		var usuario = Produto.builder()
				.codigo("10")
				.descricao("Filtro")
				.precoCusto(BigDecimal.ONE)
				.precoVenda(BigDecimal.TEN)
				.estoque(10)
				.build();
		return usuario;
		// @formatter:on
	}
}
