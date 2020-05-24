package br.com.js.base.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;

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
public class WorkDTO {
	private Long id;
	@NotBlank
	private String sku;
	@NotBlank
	private String name;
	private BigDecimal price;
	private BigDecimal discount;
}
