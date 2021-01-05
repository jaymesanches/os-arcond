package br.com.js.base.resource;

import static br.com.js.base.helper.AddressTestHelper.getAddress;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.js.base.dto.AddressDTO;
import br.com.js.base.helper.AddressTestHelper;
import br.com.js.base.model.Address;
import br.com.js.base.service.AddressService;

@ExtendWith(SpringExtension.class)
//@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
public class AddressResourceTest extends BaseResourceTest {
  private final String URL_API = "/addresses";

  @MockBean
  private AddressService service;

  private String accessToken;

  @BeforeAll
  public void setup() throws Exception {
    accessToken = obtainAccessToken("admin@admin.com", "senhas");
  }

  @Test
  @DisplayName("Deve listar todos os endereços")
  public void Should_ReturnOK_When_FindAllAddresses() throws Exception {
    // @formatter:off
    var address = getAddress(1L);
    BDDMockito.given(service.findAll()).willReturn(Arrays.asList(address));

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
  @DisplayName("Deve buscar um endereço pelo código")
  public void Should_FindAddressById() throws Exception {
    // @formatter:off
    var address = AddressTestHelper.getAddress(1l);
    given(service.findById(anyLong())).willReturn(Optional.of(address));

    var request = 
      MockMvcRequestBuilders
      .get(URL_API + "/{id}", 1l)
      .header("Authorization", "Bearer " + accessToken)
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON);

    mvc
      .perform(request)
      .andExpect(status().isOk());

    // @formatter:on
  }


  @Test
  @DisplayName("Deve criar um novo endereço")
  public void Should_RetunrCreated_When_SaveAddress() throws Exception {
    // @formatter:off
    var address = AddressTestHelper.getAddress(1l);
    var dto = AddressTestHelper.getAddressDTO();
    var json = toJson(dto);
    given(service.save(any(Address.class))).willReturn(address);

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
      ;

    // @formatter:on
  }
  
  private String toJson(AddressDTO dto) throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    var json = objectMapper.writeValueAsString(dto);
    return json;
  }

}
