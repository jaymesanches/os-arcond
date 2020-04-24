package br.com.js.base.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO {
	private Long id;
	
	@NotBlank
	private String nome;
	
	@NotBlank
	private String email;
	
	@NotBlank
	private String senha;
	
	private List<PermissaoDTO> permissoes;
}
