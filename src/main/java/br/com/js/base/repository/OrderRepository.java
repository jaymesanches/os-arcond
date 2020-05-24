package br.com.js.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
