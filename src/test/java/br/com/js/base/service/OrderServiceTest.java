package br.com.js.base.service;

import static br.com.js.base.helper.OrderTestHelper.NUMBER;
import static br.com.js.base.helper.OrderTestHelper.YEAR;
import static br.com.js.base.helper.OrderTestHelper.getOrder;
import static br.com.js.base.helper.OrderTestHelper.getOrderList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Order;
import br.com.js.base.repository.OrderRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderServiceTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  OrderService service;

  @MockBean
  OrderRepository repository;

  @Test
  @DisplayName("Deve pesquisar todas as ordens de serviço")
  public void Should_ReturnList_When_FindAllOrders() throws Exception {
    var orders = getOrderList();

    when(repository.findAll()).thenReturn(orders);

    var list = service.findAll();

    assertThat(list).isNotEmpty();
    assertThat(list).isNotEmpty();
  }

  @Test
  @DisplayName("Deve retornar uma ordem de serviço pelo id")
  public void Shoul_ReturnOrder_When_FindOrderById() throws Exception {
    var optional = Optional.of(getOrder());

    when(repository.findById(anyLong())).thenReturn((optional));

    var product = service.findById(1l);

    assertThat(product).isNotNull();
  }

  @Test
  @DisplayName("Deve retornar nulo quando pesquisar por id inválido")
  public void Should_ReturnNull_When_FindOrderByInvalidId() throws Exception {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    var product = service.findById(1l);

    assertThat(product).isNull();
  }

  @Test
  @DisplayName("Deve retornar uma lista de ordens de serviço ao pesquisa por nome")
  public void Should_ReturnOrder_When_FindOrderByNumberAndYear() throws Exception {
    when(repository.findByNumberAndYear(Mockito.anyInt(), Mockito.anyInt())).thenReturn(Optional.of(getOrder()));

    int number = 1;
    int year = 2020;
    
    var savedOrder = service.findByNumberAndYear(number, year);

    assertThat(savedOrder.getNumber()).isEqualTo(number);
    assertThat(savedOrder.getYear()).isEqualTo(year);
  }
  
  @Test
  @DisplayName("Deve retornar erro ao pesquisar por número sem número")
  public void Should_ThrowException_When_FindOrdersByNumberWithoutNumber() throws Exception {
    Throwable exception = Assertions.catchThrowable(() -> service.findByNumberAndYear(null, null));

    assertThat(exception).isInstanceOf(BusinessException.class);
    assertThat(exception).hasFieldOrPropertyWithValue("message", "Número e ano devem ser preenchidos");
  }

  @Test
  @DisplayName("Deve salvar uma Ordem de Serviço")
  public void Should_ReturnOrder_When_SaveOrder() {
    // @formatter:off
		var order = getOrder(1l);

		Mockito.when(repository.save(order)).thenReturn(order);

		var savedOrder = service.save(order);

		assertThat(savedOrder.getId()).isNotNull();
		assertThat(savedOrder.getNumber()).isEqualTo(NUMBER);
		assertThat(savedOrder.getYear()).isEqualTo(YEAR);
		// @formatter:on
  }
  
  @Test
  @DisplayName("Deve remover um produto")
  public void Should_DeleteOrder() throws Exception {
    when(repository.existsById(anyLong())).thenReturn(true);

    service.deleteById(123l);

    verify(repository, atLeastOnce()).deleteById(anyLong());
  }

  @Test
  @DisplayName("Deve retornar exceção ao tentar remover uma ordem de serviço com id inválido")
  public void Should_ThrowException_When_DeleteInvalidOrder() throws Exception {
    doNothing().when(repository).deleteById(anyLong());

    Throwable exception = Assertions.catchThrowable(() -> service.deleteById(1l));

    assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("message",
        "Ordem de Serviço inexistente");
  }

  @Test
  @DisplayName("Deve retornar erro ao criar uma ordem de serviços com dados incompletos")
  public void Should_ThrowException_When_SaveOrderWithoutNumber() {
    var order = getOrder();
    order.setNumber(null);

    when(repository.save(order)).thenThrow(DataIntegrityViolationException.class);

    Throwable exception = Assertions.catchThrowable(() -> service.save(order));

    assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("Deve alterar uma ordem de serviço")
  public void Should_ReturnUpdatedOrder_When_UpdateOrder() throws Exception {
    var order = getOrder(1l);

    when(repository.findById(1l)).thenReturn(Optional.of(order));
    when(repository.save(Mockito.any(Order.class))).thenReturn(order);

    var savedOrder = service.update(order);

    assertThat(savedOrder.getNumber().equals(order.getNumber()));
    verify(repository, Mockito.atLeastOnce()).save(order);
  }

  @Test
  @DisplayName("Deve retornar erro ao alterar uma ordem de serviço não existente")
  public void Should_ThrowException_When_UpdateInvalidOrder() throws Exception {
    var order = getOrder();

    when(repository.findById(anyLong())).thenThrow(new ResourceNotFoundException("Ordem de Serviço não existe"));

    Throwable exception = Assertions.catchThrowable(() -> service.update(order));

    assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    verify(repository, never()).save(order);
  }
}
