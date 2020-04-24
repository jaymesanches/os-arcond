package br.com.js.base.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "cliente")
@Data
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nome;

	private String email;

	private String cpf;

	@Column(name = "dta_nascimento")
	private LocalDate dtaNascimento;

	@Column(name = "dta_cadastro")
	private OffsetDateTime dtaCadastro = OffsetDateTime.now();

	private String telefone;

	@OneToMany
	private List<Endereco> enderecos;
}
