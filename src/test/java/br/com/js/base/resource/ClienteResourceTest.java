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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.js.base.dto.ClienteDTO;
import br.com.js.base.helper.ClienteTestHelper;
import br.com.js.base.model.Cliente;
import br.com.js.base.service.ClienteService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClienteResourceTest extends BaseResourceTest {

	private final String URL_API = "/clientes";

	@MockBean
	private ClienteService service;

	private String accessToken;

	@BeforeEach
	public void setup() throws Exception {
		accessToken = obtainAccessToken("admin@admin.com", "senhas");
	}

	@Test
	@DisplayName("Deve listar todos os clientes")
	public void deve_listar_todos_os_clientes() throws Exception {
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
	@DisplayName("Deve retornar erro ao criar um cliente sem nome")
	public void deve_retornar_erro_ao_criar_cliente_sem_nome() throws Exception {
		// @formatter:off
		var dto = ClienteTestHelper.getClienteDTO();
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
	@DisplayName("Deve criar um novo cliente")
	public void deve_criar_um_novo_cliente() throws Exception {
		var cliente = ClienteTestHelper.getCliente(1l);
		var dto = ClienteTestHelper.getClienteDTO();

		given(service.save(any(Cliente.class))).willReturn(cliente);

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
	@DisplayName("Deve deletar um cliente existente")
	public void deve_deletar_um_cliente() throws Exception {
		// Cenário
		var id = 123l;
		given(service.findById(anyLong())).willReturn(Cliente.builder().id(id).build());

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
	@DisplayName("Deve retornar not found ao deletar um cliente inexistente")
	public void deve_retornar_not_found_ao_deletar_um_cliente_inexistente() throws Exception {
		// Cenário
//		Todos funcionam :)
//		BDDMockito.given(service.findById(Mockito.anyLong())).willThrow(new ResourceNotFoundException());
//		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(null);
//		var mock = Mockito.mock(CadastroClienteService.class); // = @MockBean
//		Mockito.doNothing().when(service).delete(Mockito.anyLong());
		doThrow(new ResourceNotFoundException()).when(service).delete(anyLong());

		// Execução
		var request = MockMvcRequestBuilders.delete(URL_API + "/{id}", 1l).header("Authorization",
				"Bearer " + accessToken);

		mvc.perform(request).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Deve retornar uma lista de clientes por nome")
	public void deve_pesquisar_uma_lista_cliente_por_nome() throws Exception {
		var cliente = ClienteTestHelper.getCliente();
		var lista = new ArrayList<Cliente>();
		lista.add(cliente);

		// Cenário
		given(service.findByNomeIgnoreCaseContaining(anyString())).willReturn(lista);

		// Execução
		var request = MockMvcRequestBuilders.get(URL_API + "?nome=teste")
				.header("Authorization", "Bearer " + accessToken).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$..nome").exists());
	}

	@Test
	@DisplayName("Deve alterar um cliente")
	public void deve_alterar_um_cliente() throws Exception {
		// Cenário
		var cliente = ClienteTestHelper.getCliente(1l);
		cliente.setNome("Nome Alterado");
		given(service.update(any(Cliente.class))).willReturn(cliente);

		var dto = ClienteTestHelper.getClienteDTO(1l);

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
	@DisplayName("Deve buscar um cliente pelo código")
	public void deve_buscar_um_cliente_pelo_codigo() throws Exception {
		// Cenário
		var cliente = ClienteTestHelper.getCliente(1l);
		given(service.findById(anyLong())).willReturn(cliente);

		// Execução
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
			.andExpect(jsonPath("nome").value(cliente.getNome()));

        // @formatter:on
	}

	private String toJson(ClienteDTO dto) throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		var json = objectMapper.writeValueAsString(dto);
		return json;
	}
}
