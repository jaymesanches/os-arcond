package br.com.js.base.helper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import br.com.js.base.dto.OrderDTO;
import br.com.js.base.model.Order;
import br.com.js.base.model.StatusOrder;

public class OrderTestHelper {
  
  public static final int YEAR = 2020;
  public static final int NUMBER = 1;

  public static Order getOrder() {
    return getOrder(null);
  }

  public static Order getOrder(Long id) {
    // @formatter:off
    
    var order = Order.builder()
        .id(id)
        .number(NUMBER)
        .year(YEAR)
        .client(ClientTestHelper.getClient(1l))
        .price(BigDecimal.TEN)
        .status(StatusOrder.OPEN)
        .build();
    
    return order;
    // @formatter:on
  }
  
  public static OrderDTO getOrderDTO() {
    return getOrderDTO(null);
  }
  
  public static OrderDTO getOrderDTO(Long id) {
    // @formatter:off
    var dto = OrderDTO.builder()
        .id(id)
        .number(NUMBER)
        .year(YEAR)
        .client(ClientTestHelper.getClientDTO(1l))
//        .dateIn()
        .price(BigDecimal.TEN)
        .status(StatusOrder.OPEN)
        .build();
    
    return dto;
 
    // @formatter:on
  }

  public static List<Order> getOrderList() {
    var order1 = getOrder(1l);
    var order2 = getOrder(2l);
    
    return Arrays.asList(order1, order2);
  }
}
