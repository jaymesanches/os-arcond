package br.com.js.base.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ordem_servico")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServico {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer numero;
	
	private Integer ano;
	
	@ManyToOne
	private Cliente cliente;
	
	private BigDecimal preco;
	
	private BigDecimal desconto;
	
	@Column(name = "data_entrada")
	private OffsetDateTime dataEntrada;

	@Column(name = "data_entrega")
	private OffsetDateTime dataEntrega;
	
	@Column(name = "data_finalizacao")
	private OffsetDateTime dataFinalizacao;
	
	@Enumerated(EnumType.STRING)
	private StatusOrdemServico status;
}
