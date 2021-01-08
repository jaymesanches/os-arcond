package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import br.com.js.base.helper.AddressTestHelper;
import br.com.js.base.repository.AddressRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AddressServiceTest {
  
  @Autowired
  MockMvc mvc;
  
  @Autowired
  AddressService service;

  @MockBean
  AddressRepository repository;
  
  @Test
  @DisplayName("Deve pesquisar todos os clientes")
  public void Should_ReturnList_FindAllAddresses() throws Exception {
    var addresses = AddressTestHelper.getAddressesList();
    when(repository.findAll()).thenReturn(addresses);

    var list = service.findAll();

    assertThat(list).isNotEmpty();
    assertThat(list.size()).isEqualTo(2);
  }
  
  @Test
  @DisplayName("Deve retornar um endereço pelo id")
  public void Should_ReturnAddress_When_FindById() throws Exception {
    var optional = Optional.of(AddressTestHelper.getAddress());
    when(repository.findById(anyLong())).thenReturn((optional));

    var address = service.findById(1l);
    
    assertThat(address).isNotNull();
  }
  
  @Test
  @DisplayName("Deve salvar um endereço")
  public void Should_ReturnAddress_When_SaveAddress() {
    var address = AddressTestHelper.getAddress(1l);

    when(repository.save(address)).thenReturn(address);

    var savedAddress = service.save(address);

    assertThat(savedAddress.getId()).isNotNull();
  }
  

}
