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
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.js.base.dto.ServicoDTO;
import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Servico;
import br.com.js.base.service.CadastroServicoService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CadastroServicoResourceTest {
	
	// Simula as requisições http
	@Autowired
	MockMvc mvc;

	@MockBean
	CadastroServicoService service;
	
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
	@DisplayName("Deve listar todos os serviços")
	public void deve_listar_todos_os_servicos() throws Exception {
		String accessToken = obtainAccessToken();

		// @formatter:off
		MockHttpServletRequestBuilder request = 
			MockMvcRequestBuilders
			.get("/servicos")
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
	@DisplayName("Deve retornar erro ao criar um serviço sem descrição")
	public void deve_retornar_erro_ao_criar_servico_sem_descricao() throws Exception {
		var dto = Servico.builder().codigo("1").preco(BigDecimal.TEN);

		BDDMockito.given(service.save(Mockito.any(Servico.class)))
				.willThrow(new BusinessException("Dados Incompletos"));

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		String json = objectMapper.writeValueAsString(dto);

		String accessToken = obtainAccessToken(); 

		// @formatter:off
		MockHttpServletRequestBuilder request = 
			MockMvcRequestBuilders
			.post("/servicos")
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
	@DisplayName("Deve criar um novo serviço")
	public void deve_criar_um_novo_servico() throws Exception {
		String accessToken = obtainAccessToken();

		var servico = Servico.builder().codigo("1").descricao("teste").preco(BigDecimal.TEN).id(10L).build();
		var dto = ServicoDTO.builder().codigo("1").descricao("teste").preco(BigDecimal.TEN).build();

		BDDMockito.given(service.save(Mockito.any(Servico.class)))
				.willReturn(servico);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		String json = objectMapper.writeValueAsString(dto);

		// @formatter:off
		MockHttpServletRequestBuilder request = 
			MockMvcRequestBuilders
			.post("/servicos")
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
}
