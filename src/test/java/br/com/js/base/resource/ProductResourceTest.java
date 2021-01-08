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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
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

import br.com.js.base.dto.ProductDTO;
import br.com.js.base.helper.ProductTestHelper;
import br.com.js.base.model.Product;
import br.com.js.base.service.ProductService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class ProductResourceTest extends BaseResourceTest {

  private final String URL_API = "/products";

  // Simula as requisições http
  @Autowired
  MockMvc mvc;

  @MockBean
  private ProductService service;

  @Autowired
  private ModelMapper modelMapper;

  private String accessToken;

  @BeforeAll
  public void setup() throws Exception {
    accessToken = obtainAccessToken("admin@admin.com", "senhas");
  }

  @Test
  @DisplayName("Deve listar todos os produtos")
  public void Should_ReturnOK_When_FindAllProducts() throws Exception {
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
  @DisplayName("Deve retornar erro ao criar um produto sem nome")
  public void Should_ThrowException_When_SaveProductWithoutName() throws Exception {
    // @formatter:off
		var dto = ProductTestHelper.getProductDTO();
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
			.andReturn().getResolvedException()			;
		
		assertThat(result).isInstanceOf(MethodArgumentNotValidException.class);
		
		// @formatter:on
  }

  @Test
  @DisplayName("Deve criar um novo produto")
  public void Should_ReturnCreated_When_SaveProduct() throws Exception {
    // @formatter:off
    var product = ProductTestHelper.getProduct(1l);
    var dto = ProductTestHelper.getProductDTO(1l);
    mockModelMapper(product, dto);
    
    given(service.save(any())).willReturn(product);
    
    System.out.println(modelMapper);

    var json = toJson(dto);

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
  @DisplayName("Deve deletar um produto existente")
  public void Should_ReturnNoContent_When_DeleteProduct() throws Exception {
    // @formatter:off
    var id = 123l;
    given(service.findById(anyLong())).willReturn(Product.builder().id(id).build());

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
  @DisplayName("Deve retornar not found ao deletar um produto inexistente")
  public void Should_ReturnNotFound_When_DeleteInvalidProduct() throws Exception {
    // @formatter:off
    doThrow(new ResourceNotFoundException()).when(service).delete(anyLong());

    var request = MockMvcRequestBuilders.delete(URL_API + "/{id}", 1l).header("Authorization", "Bearer " + accessToken);

    mvc.perform(request)
      .andExpect(status().isNotFound());
    // @formatter:on
  }

  @Test
  @DisplayName("Deve retornar uma lista de produtos por nome")
  public void Should_ReturnOK_When_FindProductsByName() throws Exception {
    // @formatter:off

		var product = ProductTestHelper.getProduct();
		var list = new ArrayList<Product>();
		list.add(product);

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
  @DisplayName("Deve alterar um produto")
  public void Should_ReturnOK_When_UpdateProduct() throws Exception {
    // @formatter:off
    var product = ProductTestHelper.getProduct(1l);
    product.setName("Descrição Alterada");
    var dto = ProductTestHelper.getProductDTO(1l);
    dto.setName("Descrição Alterada");
    var json = toJson(dto);
    
    mockModelMapper(product, dto);
    
    given(service.update(any(Product.class))).willReturn(product);

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
  @DisplayName("Deve buscar um produto pelo código")
  public void Should_ReturnOK_When_FindProductById() throws Exception {
    // @formatter:off
    var product = ProductTestHelper.getProduct(1l);
    var dto = ProductTestHelper.getProductDTO(1l);
    
    mockModelMapper(product, dto);
    given(service.findById(anyLong())).willReturn(product);

		var request = 
			MockMvcRequestBuilders
			.get(URL_API + "/{id}", 1l)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);

		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("name").value(product.getName()));

    // @formatter:on
  }

  @Test
  @DisplayName("Deve retornar not found ao buscar um produto pelo código inválido")
  public void Should_ReturnNotFound_When_FindProductWithInvalidId() throws Exception {
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

  private String toJson(ProductDTO dto) throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
    var json = objectMapper.writeValueAsString(dto);
    return json;
  }
  
  private void mockModelMapper(Product product, ProductDTO dto) {
    given(modelMapper.map(any(ProductDTO.class), any())).willReturn(product);
    given(modelMapper.map(any(Product.class), any())).willReturn(dto);
  }
}
