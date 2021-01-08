package br.com.js.base.resource;

import static br.com.js.base.helper.ClientTestHelper.getClient;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.modelmapper.ModelMapper;
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

import br.com.js.base.dto.ClientDTO;
import br.com.js.base.dto.WorkDTO;
import br.com.js.base.helper.AddressTestHelper;
import br.com.js.base.helper.ClientTestHelper;
import br.com.js.base.model.Client;
import br.com.js.base.model.Work;
import br.com.js.base.service.AddressService;
import br.com.js.base.service.ClientService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ClientResourceTest extends BaseResourceTest {
  
  private final String URL_API = "/clients";

  @Autowired
  MockMvc mvc;
  
  @Autowired
  private ModelMapper modelMapper;
  
  @MockBean
  private ClientService service;
  
  @MockBean
  private AddressService addressService;

  private String accessToken;

  @BeforeAll
  public void setup() throws Exception {
    accessToken = obtainAccessToken("admin@admin.com", "senhas");
  }

  @Test
  @DisplayName("Deve listar todos os clientes")
  public void Should_ReturnOK_When_FindAllClients() throws Exception {
    // @formatter:off
    var client = getClient(1L);
    BDDMockito.given(service.findAll()).willReturn(Arrays.asList(client));
    
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
  public void Shoud_ReturnBadRequest_When_SaveClientWithoutName() throws Exception {
    // @formatter:off
		var dto = ClientTestHelper.getClientDTO();
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
  @DisplayName("Deve criar um novo cliente")
  public void Should_RetunrCreated_When_SaveClient() throws Exception {
    var client = ClientTestHelper.getClient(1l);
    var dto = ClientTestHelper.getClientDTO(1l);
    var json = toJson(dto);

    mockModelMapper(client, dto);
    given(service.save(any(Client.class))).willReturn(client);


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
			.andExpect(jsonPath("name").value(dto.getName()));

		// @formatter:on
  }

  @Test
  @DisplayName("Deve deletar um cliente existente")
  public void Should_ReturnNoContent_When_DeleteClient() throws Exception {
    // @formatter:off
    var id = 123l;
    given(service.findById(anyLong())).willReturn(Client.builder().id(id).build());

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
  public void Should_ReturnNorFound_When_DeleteInvalidClient() throws Exception {
//		Todos funcionam :)
//		BDDMockito.given(service.findById(Mockito.anyLong())).willThrow(new ResourceNotFoundException());
//		BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(null);
//		var mock = Mockito.mock(CadastroClienteService.class); // = @MockBean
//		Mockito.doNothing().when(service).delete(Mockito.anyLong());
    doThrow(new ResourceNotFoundException()).when(service).delete(anyLong());

    var request = MockMvcRequestBuilders.delete(URL_API + "/{id}", 1l).header("Authorization", "Bearer " + accessToken);

    mvc.perform(request).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Deve retornar uma lista de clientes por nome")
  public void Should_FindClientByName() throws Exception {
    // @formatter:off
    var client = ClientTestHelper.getClient();
    var list = new ArrayList<Client>();
    list.add(client);

    given(service.findByNameIgnoreCaseContaining(anyString())).willReturn(list);

    var request = 
        MockMvcRequestBuilders
        .get(URL_API + "?name=teste")
        .header("Authorization", "Bearer " + accessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON);

    mvc.
      perform(request)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$..name").exists());
    // @formatter:on
  }

  @Test
  @DisplayName("Deve alterar um cliente")
  public void Should_UpdateClient() throws Exception {
    // @formatter:off
    var client = ClientTestHelper.getClient(1l);
    client.setName("Nome Alterado");
    
    var dto = ClientTestHelper.getClientDTO(1l);
    dto.setName("Nome Alterado");
    var json = toJson(dto);
    
    mockModelMapper(client, dto);
    given(service.update(any(Client.class))).willReturn(client);

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
			.andExpect(jsonPath("name").value("Nome Alterado"));
		// @formatter:on
  }

  @Test
  @DisplayName("Deve buscar um cliente pelo código")
  public void Should_FindClientById() throws Exception {
    // @formatter:off
    var client = ClientTestHelper.getClient(1l);
    var dto = ClientTestHelper.getClientDTO(1l);
    
    mockModelMapper(client, dto);
    given(service.findById(anyLong())).willReturn(client);

		var request = 
			MockMvcRequestBuilders
			.get(URL_API + "/{id}", 1l)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);

		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(client.getName()));

    // @formatter:on
  }

  @Test
  @DisplayName("Deve retornar not found ao buscar um cliente pelo código inválido")
  public void Should_ReturnNotFound_When_FindByInvalidId() throws Exception {
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

  @Test
  @DisplayName("Deve pesquisar endereços do cliente")
  public void Should_FindClientAdresses() throws Exception {
    // @formatter:off
    var client = ClientTestHelper.getClient(1l);
    client.setAddresses(AddressTestHelper.getAddressesList());

    given(service.findById(anyLong())).willReturn(client);

    var request = 
      MockMvcRequestBuilders
      .get(URL_API + "/{id}/addresses", 1l)
      .header("Authorization", "Bearer " + accessToken)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON);

    mvc
      .perform(request)
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[*].id", org.hamcrest.Matchers.contains(1, 2)));
    // @formatter:on
  }

  private String toJson(ClientDTO dto) throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    var json = objectMapper.writeValueAsString(dto);
    return json;
  }
  
  private void mockModelMapper(Client client, ClientDTO dto) {
    given(modelMapper.map(any(ClientDTO.class), any())).willReturn(client);
    given(modelMapper.map(any(Client.class), any())).willReturn(dto);
  }
}
