package br.com.js.base.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import br.com.js.base.model.StatusOrder;
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
public class OrderDTO {
  private Long id;
  
  @NotNull
  private Integer number;
  @NotNull
  private Integer year;
  @NotNull
  private ClientDTO client;
  private BigDecimal price;
  private BigDecimal discount;
  private String dateIn;
  private String dateOut;
  private String dateEnd;
  private StatusOrder status;
}
