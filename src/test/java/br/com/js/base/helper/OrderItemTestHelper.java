package br.com.js.base.helper;

import java.math.BigDecimal;

import br.com.js.base.model.OrderItem;

public class OrderItemTestHelper {

  public static OrderItem getOrderItem() {
    return getOrderItem(null);
  }
  
  public static OrderItem getOrderItem(Long id) {
    return OrderItem.builder()
      .id(id)
      .sequence(1)
      .amount(1)
      .price(BigDecimal.TEN)
      .product(ProductTestHelper.getProduct(1l))
      .build();
  }
}
