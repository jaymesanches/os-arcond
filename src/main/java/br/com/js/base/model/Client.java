package br.com.js.base.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
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
@Table(name = "os_client")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String name;

  private String email;

  private String document;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name = "created_at")
  @Builder.Default
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @EmbeddedId
  private Phone phone;

  @OneToMany(mappedBy = "client")
  private List<Address> addresses;

  public static int compareByName(Client o1, Client o2) {
    return o1.name.compareTo(o2.name);
  }
}
