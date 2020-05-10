package br.com.js.base.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String nome;

	private String email;

	private String cpf;

//	@Column(name = "data_nascimento")
//	private LocalDate dataNascimento;
//
//	@Column(name = "data_cadastro")
//	@Builder.Default
//	private OffsetDateTime dataCadastro = OffsetDateTime.now();

	private String telefone;

	@OneToMany
	private List<Endereco> enderecos;
}
