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

import org.assertj.core.api.Assertions;
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

import br.com.js.base.dto.WorkDTO;
import br.com.js.base.exception.BusinessException;
import br.com.js.base.helper.WorkTestHelper;
import br.com.js.base.model.Work;
import br.com.js.base.service.WorkService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WorkResourceTest extends BaseResourceTest {

  private final String URL_API = "/works";

  // Simula as requisições http
  @Autowired
  MockMvc mvc;

  @MockBean
  WorkService service;

  private String accessToken;

  @BeforeEach
  public void setup() throws Exception {
    accessToken = obtainAccessToken("admin@admin.com", "senhas");
  }

  @Test
  @DisplayName("Deve listar todos os serviços")
  public void Should_ReturnOK_When_FindAllWorks() throws Exception {
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
  public void Should_ThrowException_When_SaveWorkWithoutName() throws Exception {
    // @formatter:off
		var dto = WorkTestHelper.getWorkDTO();
		dto.setName(null);
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
  public void Should_ReturnCreated_When_SaveWork() throws Exception {
    // @formatter:off
    var work = WorkTestHelper.getWork(1l);
    var dto = WorkTestHelper.getWorkDTO();
    var json = toJson(dto);

    given(service.save(Mockito.any(Work.class))).willReturn(work);

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
			.andExpect(jsonPath("name").value(dto.getName()));

		// @formatter:on
  }

  @Test
  @DisplayName("Deve deletar um serviço existente")
  public void Should_ReturnNoContent_When_DeleteWork() throws Exception {
    // @formatter:off
    var id = 123l;
    given(service.findById(anyLong())).willReturn(Work.builder().id(id).build());

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
  public void Should_ReturnNotFound_When_DeleteInvalidWork() throws Exception {
    // @formatter:off
		doThrow(new ResourceNotFoundException()).when(service).delete(anyLong());

		var request = MockMvcRequestBuilders
				.delete(URL_API + "/{id}", 1l)
				.header("Authorization", "Bearer " + accessToken);

		mvc.perform(request)
			.andExpect(status().isNotFound());
		// @formatter:on
  }

  @Test
  @DisplayName("Deve retornar uma lista de serviços por descrição")
  public void Should_ReturnOK_When_FindWorksByName() throws Exception {
    // @formatter:off
		var work = WorkTestHelper.getWork();
		var list = new ArrayList<Work>();
		list.add(work);

		given(service.findByNameIgnoreCaseContaining(anyString())).willReturn(list);

		var request = MockMvcRequestBuilders
				.get(URL_API + "?name=teste")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$..name").exists());
		// @formatter:on
  }
  
  @Test
  @DisplayName("Deve retornar erro ao pesquisar por nome sem nome")
  public void Should_ThrowException_When_FindWorksByNameWithoutName() throws Exception {
    Throwable exception = Assertions.catchThrowable(() -> service.findByNameIgnoreCaseContaining(null));

    assertThat(exception).isInstanceOf(BusinessException.class);
    assertThat(exception).hasFieldOrPropertyWithValue("message", "Nome precisa ser preenchido");
  }

  @Test
  @DisplayName("Deve alterar um serviço")
  public void Should_ReturnOK_When_UpdateWork() throws Exception {
    // @formatter:off
		var work = WorkTestHelper.getWork(1l);
		work.setName("Descrição Alterada");
		given(service.update(any(Work.class))).willReturn(work);

		var dto = WorkTestHelper.getWorkDTO(1l);
		var json = toJson(dto);

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
			.andExpect(jsonPath("name").value("Descrição Alterada"));

		// @formatter:on
  }

  @Test
  @DisplayName("Deve buscar um serviço pelo código")
  public void Should_ReturnOK_When_FindWorkById() throws Exception {
    // @formatter:off
		var work = WorkTestHelper.getWork(1l);
		given(service.findById(anyLong())).willReturn(work);

		var request = 
			MockMvcRequestBuilders
			.get(URL_API + "/{id}", 1l)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);

		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(work.getName()));

    // @formatter:on
  }

  @Test
  @DisplayName("Deve retornar not found ao buscar um servico pelo código inválido")
  public void deve_retornar_not_found_ao_buscar_um_servico_pelo_codigo_invalido() throws Exception {
    // @formatter:off
    given(service.findById(anyLong())).willReturn(null);
    
    var request = 
      MockMvcRequestBuilders
      .get(URL_API + "/{id}", 999l)
      .header("Authorization", "Bearer " + accessToken)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON);

    mvc
      .perform(request)
      .andExpect(status().isNotFound());

    // @formatter:on
  }

  private String toJson(WorkDTO dto) throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    var json = objectMapper.writeValueAsString(dto);
    return json;
  }
}
