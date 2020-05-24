package br.com.js.base.dto;

import javax.validation.constraints.NotBlank;

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
public class AddressDTO {
	private Long id;

	@NotBlank
	private String cep;
	
	@NotBlank
	private String street;
	
	@NotBlank
	private String neighborhood;
	
	@NotBlank
	private String city;
	
	@NotBlank
	private String state;
}
