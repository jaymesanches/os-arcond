package br.com.js.base.dto;

import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EnderecoDTO {
	private Long id;

	@NotBlank
	private String cep;
	
	@NotBlank
	private String logradouro;
	
	private String complemento;
	
	@NotBlank
	private String bairro;
	
	@NotBlank
	private String localidade;
	
	@NotBlank
	private String uf;
	
	private String unidade;
}
