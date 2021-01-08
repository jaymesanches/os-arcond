package br.com.js.base.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
  private Long id;
  private Integer sequence;
  private Integer amount;
  private Integer number;
  private Integer year;
  private BigDecimal price;

  @NotNull
  private ProductDTO product;
}
