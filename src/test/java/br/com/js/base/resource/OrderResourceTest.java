package br.com.js.base.resource;

import static br.com.js.base.helper.OrderTestHelper.getOrder;
import static br.com.js.base.helper.OrderTestHelper.getOrderDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.js.base.dto.OrderDTO;
import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Order;
import br.com.js.base.service.OrderService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class OrderResourceTest extends BaseResourceTest {

  private final String URL_API = "/orders";

  @MockBean
  private OrderService service;

  private String accessToken;

  @BeforeAll
  public void setup() throws Exception {
    accessToken = obtainAccessToken("admin@admin.com", "senhas");
  }
  
  @Test
  @DisplayName("Deve listar todas as ordens de serviço")
  public void Should_ReturnOK_When_FindAllOrders() throws Exception {
    // @formatter:off
    
    var order = getOrder(1l);
    
    BDDMockito.given(service.findAll()).willReturn(Arrays.asList(order));
    
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
  @DisplayName("Deve listar todas as ordens de serviço")
  public void Should_ReturnNotFound_When_NotFindAnyOrders() throws Exception {
    // @formatter:off
    BDDMockito.given(service.findAll()).willReturn(null);
    
    var request = 
        MockMvcRequestBuilders
        .get(URL_API)
        .header("Authorization", "Bearer " + accessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        ;
    
    mvc
    .perform(request)
    .andExpect(status().isNotFound());
    // @formatter:on
  }
  
  @Test
  @DisplayName("Deve salvar uma ordem de serviço")
  public void Should_ReturnCreated_When_SaveOrder() throws Exception {
    // @formatter:off
    var order = getOrder(1l);
    var dto = getOrderDTO();
    var json = toJson(dto);
    
    BDDMockito.given(service.save(Mockito.any(Order.class))).willReturn(order);
    
    var request = 
        MockMvcRequestBuilders
        .post(URL_API)
        .header("Authorization", "Bearer " + accessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json);

      mvc
        .perform(request)
        .andExpect(status().isCreated());
      // @formatter:on
  }
  
  @Test
  @DisplayName("Deve retornar erro ao salvar ordem de serviço sem cliente")
  public void Should_ThrowException_When_SaveOrderWithoutClient() throws Exception {
    // @formatter:off
    var dto = getOrderDTO();
    dto.setClient(null);
    
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
  @DisplayName("Deve deletar um ordem de serviço existente")
  public void Should_ReturnNoContent_When_DeleteOrder() throws Exception {
    // @formatter:off
    var id = 123l;
    given(service.findById(anyLong())).willReturn(Order.builder().id(id).build());

    var request = 
      MockMvcRequestBuilders
      .delete(URL_API + "/{id}", 1l)
      .header("Authorization", "Bearer " + accessToken)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON);

    mvc
      .perform(request)
      .andExpect(status().isNoContent());

    // @formatter:on
  }
  
  @Test
  @DisplayName("Deve retornar not found ao deletar uma ordem de serviço inexistente")
  public void Should_ReturnNotFound_When_DeleteInvalidOrder() throws Exception {
    // @formatter:off
//    doThrow(new ResourceNotFoundException()).when(service).deleteById(anyLong()); //404
    doThrow(new BusinessException("Ordem de Serviço inexistente")).when(service).deleteById(anyLong()); //400

    var request = 
        MockMvcRequestBuilders
        .delete(URL_API + "/{id}", 1)
        .header("Authorization", "Bearer " + accessToken);

    mvc.perform(request).andExpect(status().isBadRequest());
    // @formatter:on
  }
  
  @Test
  @DisplayName("Deve alterar uma ordem de serviço")
  public void Should_UpdateOrder() throws Exception {
    // @formatter:off
    var order = getOrder(1l);
    order.setPrice(BigDecimal.ONE);
    
    var dto = getOrderDTO(1l);
    var json = toJson(dto);
    
    given(service.update(any(Order.class))).willReturn(order);

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
      .andExpect(jsonPath("price").value(BigDecimal.ONE));
    // @formatter:on
  }

  @Test
  @DisplayName("Deve retornar ok quando buscar por id")
  public void Should_ReturnOk_When_FindOrderById() throws Exception {
    // @formatter:off
    var order = getOrder(1l);
    
    given(service.findById(anyLong())).willReturn(order);

    var request = 
      MockMvcRequestBuilders
      .get(URL_API + "/{id}", 1l)
      .header("Authorization", "Bearer " + accessToken)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON);

    mvc
      .perform(request)
      .andExpect(status().isOk())
      .andExpect(jsonPath("number").value(order.getNumber()));

    // @formatter:on
  }

  @Test
  @DisplayName("Deve retornar not found ao buscar uma ordem de serviço com id inválido")
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
  
  private String toJson(OrderDTO dto) throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    var json = objectMapper.writeValueAsString(dto);
    return json;
  }
}
