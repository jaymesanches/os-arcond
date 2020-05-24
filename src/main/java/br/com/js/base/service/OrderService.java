package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.js.base.model.Order;
import br.com.js.base.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;

	public List<Order> findAll() {
		return repository.findAll();
	}
	
	public Order findById(Long id) {
		Optional<Order> orderOptional = repository.findById(id);
		return orderOptional.orElse(null);
	}

	public Order save(Order os) {
		return repository.save(os);
	}
}
