package br.com.js.base.model;

import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "os_order_item")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private Integer sequence;
  
  private Integer amount;

  @Column(name = "price")
  private BigDecimal price;
  
  @ManyToOne
  @JoinColumn(name = "id_order")
  private Order order;
  
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "id_product", referencedColumnName = "id")
  private Product product;
  
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "id_work", referencedColumnName = "id")
  private Work work;

}
