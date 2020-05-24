package br.com.js.base.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {
	private Long id;
	
	@NotBlank
	private String name;
}
