package br.com.js.base.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDTO {
	private Long id;
	
	@NotBlank
	private String nome;
	
	@NotBlank
	@Email
	private String email;
	
	private String cpf;
	
	@NotBlank
	private String telefone;

	private LocalDate dtaNascimento;
	
	private OffsetDateTime dtaCadastro;
}
