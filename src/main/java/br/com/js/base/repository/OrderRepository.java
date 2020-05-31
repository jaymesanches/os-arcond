package br.com.js.base.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

  Optional<Order> findByNumberAndYear(Integer number, Integer year);

}
