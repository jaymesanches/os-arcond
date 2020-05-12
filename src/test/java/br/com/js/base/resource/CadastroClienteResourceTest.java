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
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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

import br.com.js.base.dto.ClienteDTO;
import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Cliente;
import br.com.js.base.service.CadastroClienteService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CadastroClienteResourceTest extends BaseResourceTest {

	private final String URL_API = "/clientes";

	// Simula as requisições http
	@Autowired
	MockMvc mvc;

	@MockBean
	CadastroClienteService service;

	protected String obtainAccessToken() throws Exception {
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
	@DisplayName("Deve listar todos os clientes")
	public void deve_listar_todos_os_clientes() throws Exception {
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
	@DisplayName("Deve retornar erro ao criar um cliente sem nome")
	public void deve_retornar_erro_ao_criar_cliente_sem_nome() throws Exception {
		var dto = novoClienteDTO();

		BDDMockito.given(service.save(Mockito.any(Cliente.class)))
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
	@DisplayName("Deve criar um novo cliente")
	public void deve_criar_um_novo_cliente() throws Exception {
		String accessToken = obtainAccessToken();

		var cliente = novoCliente();
		cliente.setId(10l);
		var dto = novoClienteDTO();

		BDDMockito.given(service.save(Mockito.any(Cliente.class))).willReturn(cliente);

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

	@Test
	@DisplayName("Deve deletar um cliente existente")
	public void deve_deletar_um_cliente() throws Exception {
		String accessToken = obtainAccessToken();

		// Cenário
		Long id = 123l;
		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Cliente.builder().id(id).build());

		// Execução

		// @formatter:off
		MockHttpServletRequestBuilder request = 
			MockMvcRequestBuilders
			.delete(URL_API + "/1")
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);

		mvc
			.perform(request)
			.andExpect(status().isNoContent());

		// @formatter:on
	}

	@Test
	@DisplayName("Deve retornar not found ao deletar um cliente inexistente")
	public void deve_retornar_not_found_ao_deletar_um_cliente_inexistente() throws Exception {
		var accessToken = obtainAccessToken();

		// Cenário
//		Todos funcionam :)
//		BDDMockito.given(service.findById(Mockito.anyLong())).willThrow(new ResourceNotFoundException());
//		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(null);
//		var mock = Mockito.mock(CadastroClienteService.class); // = @MockBean
//		Mockito.doNothing().when(service).delete(Mockito.anyLong());
		Mockito.doThrow(new ResourceNotFoundException()).when(service).delete(Mockito.anyLong());
		
		// Execução
		var request = MockMvcRequestBuilders.delete(URL_API + "/1").header("Authorization", "Bearer " + accessToken);

		mvc.perform(request).andExpect(status().isNotFound());
	}

	private String toJson(ClienteDTO dto) throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		var json = objectMapper.writeValueAsString(dto);
		return json;
	}

	private ClienteDTO novoClienteDTO() {
		// @formatter:off
		var dto = ClienteDTO.builder()
					.nome("Jayme Sanches")
					.cpf("12345678909")
					.email("jayme@email.com")
					.telefone("55554433")
					.build();
		return dto;
		// @formatter:on
	}

	private Cliente novoCliente() {
		// @formatter:off
		var cliente = Cliente.builder()
				.nome("Jayme Sanches")
				.cpf("12345678909")
				.email("jayme@email.com")
				.telefone("55554433")
				.build();
		return cliente;
		// @formatter:on
	}
}

//Cenário

//Execução

//Verificação