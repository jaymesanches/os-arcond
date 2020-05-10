package br.com.js.base.dto;

import java.math.BigDecimal;

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
public class ServicoDTO {
	
	private Long id;
	private String codigo;
	private String descricao;
	private BigDecimal preco;
	private BigDecimal desconto;
}
