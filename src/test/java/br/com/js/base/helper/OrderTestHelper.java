package br.com.js.base.helper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import br.com.js.base.model.Order;
import br.com.js.base.model.StatusOrder;

public class OrderTestHelper {
  
  public static final int YEAR = 2020;
  public static final int NUMBER = 1;

  public static Order getOrder() {
    return getOrder(null);
  }

  /**
   * @param id
   * @return
   */
  public static Order getOrder(Long id) {
    // @formatter:off
    var order = Order.builder()
        .id(id)
        .number(NUMBER)
        .year(YEAR)
        .client(ClientTestHelper.getClient(1l))
        .dateIn(OffsetDateTime.now())
        .price(BigDecimal.TEN)
        .status(StatusOrder.ABERTA)
        .build();
    
    return order;
    // @formatter:on
  }
}
