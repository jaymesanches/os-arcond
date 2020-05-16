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
import org.mockito.Mockito;
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

import br.com.js.base.dto.ServicoDTO;
import br.com.js.base.helper.ServicoTestHelper;
import br.com.js.base.model.Servico;
import br.com.js.base.service.ServicoService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ServicoResourceTest extends BaseResourceTest {

	private final String URL_API = "/servicos";
	
	// Simula as requisições http
	@Autowired
	MockMvc mvc;

	@MockBean
	ServicoService service;
	
	private String accessToken;

	@BeforeEach
	public void setup() throws Exception {
		accessToken = obtainAccessToken("admin@admin.com", "senhas");
	}

	@Test
	@DisplayName("Deve listar todos os serviços")
	public void deve_listar_todos_os_servicos() throws Exception {
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
	@DisplayName("Deve retornar erro ao criar um serviço sem descrição")
	public void deve_retornar_erro_ao_criar_servico_sem_descricao() throws Exception {
		// @formatter:off
		var dto = ServicoTestHelper.getServicoDTO();
		dto.setDescricao(null);

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
	@DisplayName("Deve criar um novo serviço")
	public void deve_criar_um_novo_servico() throws Exception {
		var servico = ServicoTestHelper.getServico(1l);
		var dto = ServicoTestHelper.getServicoDTO();

		given(service.save(Mockito.any(Servico.class))).willReturn(servico);

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
			.andExpect(jsonPath("descricao").value(dto.getDescricao()));

		// @formatter:on
	}

	@Test
	@DisplayName("Deve deletar um serviço existente")
	public void deve_deletar_um_servico() throws Exception {
		// Cenário
		var id = 123l;
		given(service.findById(anyLong())).willReturn(Servico.builder().id(id).build());

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
	@DisplayName("Deve retornar not found ao deletar um serviço inexistente")
	public void deve_retornar_not_found_ao_deletar_um_servico_inexistente() throws Exception {
		// @formatter:off
		// Cenário
		doThrow(new ResourceNotFoundException()).when(service).delete(anyLong());

		// Execução
		var request = MockMvcRequestBuilders
				.delete(URL_API + "/{id}", 1l)
				.header("Authorization", "Bearer " + accessToken);

		mvc.perform(request)
			.andExpect(status().isNotFound());
		// @formatter:on
	}

	@Test
	@DisplayName("Deve retornar uma lista de serviços por descrição")
	public void deve_pesquisar_uma_lista_de_servicos_por_descricao() throws Exception {
		// @formatter:off

		var servico = ServicoTestHelper.getServico();
		var lista = new ArrayList<Servico>();
		lista.add(servico);

		// Cenário
		given(service.findByDescricaoIgnoreCaseContaining(anyString())).willReturn(lista);

		// Execução
		var request = MockMvcRequestBuilders
				.get(URL_API + "?descricao=teste")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$..descricao").exists());
		// @formatter:on
	}

	@Test
	@DisplayName("Deve alterar um serviço")
	public void deve_alterar_um_servico() throws Exception {
		// @formatter:off
		// Cenário
		var servico = ServicoTestHelper.getServico(1l);
		servico.setDescricao("Descrição Alterada");
		given(service.update(any(Servico.class))).willReturn(servico);

		var dto = ServicoTestHelper.getServicoDTO(1l);

		var json = toJson(dto);

		// Execução
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
			.andExpect(jsonPath("descricao").value("Descrição Alterada"));

		// @formatter:on
	}

	@Test
	@DisplayName("Deve buscar um serviço pelo código")
	public void deve_buscar_um_produto_pelo_codigo() throws Exception {
		// @formatter:off
		// Cenário
		var servico = ServicoTestHelper.getServico(1l);
		given(service.findById(anyLong())).willReturn(servico);

		// Execução
		var request = 
			MockMvcRequestBuilders
			.get(URL_API + "/{id}", 1l)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);

		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("descricao").value(servico.getDescricao()));

        // @formatter:on
	}

	private String toJson(ServicoDTO dto) throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		var json = objectMapper.writeValueAsString(dto);
		return json;
	}
}
