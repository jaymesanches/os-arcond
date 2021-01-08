package br.com.js.base.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.data.annotation.Immutable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Immutable
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Phone {
  @Column(name = "phone")
  private String number;
}
