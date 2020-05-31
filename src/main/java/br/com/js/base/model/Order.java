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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "os_order")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Integer number;
	
	private Integer year;
	
	@ManyToOne
	@JoinColumn(name = "id_client")
	private Client client;
	
	private BigDecimal price;
	
	private BigDecimal discount;
	
	@Column(name = "date_in")
	private OffsetDateTime dateIn;

	@Column(name = "date_out")
	private OffsetDateTime dateOut;
	
	@Column(name = "date_end")
	private OffsetDateTime dateEnd;
	
	@Enumerated(EnumType.STRING)
	private StatusOrder status;
}
