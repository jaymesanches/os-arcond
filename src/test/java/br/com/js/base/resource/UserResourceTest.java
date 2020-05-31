package br.com.js.base.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.assertj.core.api.Assertions;
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

import br.com.js.base.dto.UserDTO;
import br.com.js.base.exception.BusinessException;
import br.com.js.base.helper.UserTestHelper;
import br.com.js.base.model.User;
import br.com.js.base.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserResourceTest extends BaseResourceTest {

  private final String URL_API = "/users";

  // Simula as requisições http
  @Autowired
  MockMvc mvc;

  @MockBean
  UserService service;

  private String accessToken;

  @BeforeEach
  public void setup() throws Exception {
    accessToken = obtainAccessToken("admin@admin.com", "senhas");
  }

  @Test
  @DisplayName("Deve listar todos os usuários")
  public void Should_ReturnOK_When_findAllUsers() throws Exception {
    // @formatter:off
    
    when(service.findAll()).thenReturn(UserTestHelper.getUserList());
    
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
  public void Should_ThrowException_When_SaveUserWithoutName() throws Exception {
    // @formatter:off
		var dto = UserTestHelper.getUserDTO();
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
  @DisplayName("Deve criar um novo produto")
  public void Should_ReturnCreated_When_SaveProduct() throws Exception {
    // @formatter:off
    var user = UserTestHelper.getUser(1l);
    var dto = UserTestHelper.getUserDTO();
    var json = toJson(dto);

    given(service.save(any(User.class))).willReturn(user);

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
  @DisplayName("Deve deletar um usuário existente")
  public void Should_ReturnNoContent_When_DeleteUser() throws Exception {
    // @formatter:off
    var id = 123l;
    
    given(service.findById(anyLong())).willReturn(User.builder().id(id).build());

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
  public void Should_ReturnNotFound_When_DeleteInvalidUser() throws Exception {
    // @formatter:off
		doThrow(new ResourceNotFoundException()).when(service).delete(anyLong());

		var request = 
			MockMvcRequestBuilders
				.delete(URL_API + "/{id}", 1l)
				.header("Authorization", "Bearer " + accessToken);

		mvc.perform(request).andExpect(status().isNotFound());
		// @formatter:on
  }

  @Test
  @DisplayName("Deve retornar uma lista de usuarios por nome")
  public void Should_ReturnOk_When_FindUsersByName() throws Exception {
    // @formatter:off
		var user = UserTestHelper.getUser();
		var list = new ArrayList<User>();
		list.add(user);

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
  public void Should_ThrowException_When_FindUsersByNameWithoutName() throws Exception {
    Throwable exception = Assertions.catchThrowable(() -> service.findByNameIgnoreCaseContaining(null));

    assertThat(exception).isInstanceOf(BusinessException.class);
    assertThat(exception).hasFieldOrPropertyWithValue("message", "Nome precisa ser preenchido");
  }

  @Test
  @DisplayName("Deve alterar um usuário")
  public void Should_ReturnOk_When_UpdateUser() throws Exception {
    // @formatter:off
    var user = UserTestHelper.getUser(1l);
    user.setName("Nome Alterado");
    var dto = UserTestHelper.getUserDTO(1l);
    var json = toJson(dto);
    
    given(service.update(any(User.class))).willReturn(user);

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
  @DisplayName("Deve buscar um usuário pelo código")
  public void Should_ReturnOk_When_FindUserById() throws Exception {
    // @formatter:off
    var user = UserTestHelper.getUser(1l);
    
    given(service.findById(anyLong())).willReturn(user);

    var request = 
    	MockMvcRequestBuilders
    	.get(URL_API + "/{id}", 1l)
    	.header("Authorization", "Bearer " + accessToken)
    	.contentType(MediaType.APPLICATION_JSON)
    	.accept(MediaType.APPLICATION_JSON);
    
    mvc
    	.perform(request)
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("name").value(user.getName()));
    // @formatter:on
  }

  @Test
  @DisplayName("Deve retornar not found ao buscar um usuário pelo código inválido")
  public void Should_ReturnNotFound_When_FindUserWithInvalidId() throws Exception {
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

  private String toJson(UserDTO dto) throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    var json = objectMapper.writeValueAsString(dto);
    return json;
  }
}
