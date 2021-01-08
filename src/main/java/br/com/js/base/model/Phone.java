package br.com.js.base.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.data.annotation.Immutable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Immutable
@AllArgsConstructor
@Getter
@NoArgsConstructor(staticName = "private") // Makes MyValueObject() private.
public class Phone {
  @Column(name = "phone")
  private String number;

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }
}
