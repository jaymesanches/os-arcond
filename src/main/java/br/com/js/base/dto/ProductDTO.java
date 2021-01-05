package br.com.js.base.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
  
	private Long id;
	
	@NotBlank
	private String sku;
	
	@NotBlank
	private String name;
	
	private BigDecimal costPrice;
	private BigDecimal salePrice;
	
	@NotNull
	private Integer stock;
}
