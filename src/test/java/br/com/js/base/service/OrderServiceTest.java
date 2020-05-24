package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import br.com.js.base.helper.OrderTestHelper;
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
	@DisplayName("Deve salvar uma Ordem de Serviço")
	public void Should_ReturnOrder_When_SaveOrder() {
	  // @formatter:off
		var order = OrderTestHelper.getOrder(1l);

		Mockito.when(repository.save(order)).thenReturn(order);

		var savedOrder = service.save(order);

		assertThat(savedOrder.getId()).isNotNull();
		assertThat(savedOrder.getNumber()).isEqualTo(OrderTestHelper.NUMBER);
		assertThat(savedOrder.getYear()).isEqualTo(OrderTestHelper.YEAR);
		// @formatter:on
	}

}
