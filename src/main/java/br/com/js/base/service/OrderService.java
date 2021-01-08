package br.com.js.base.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Order;
import br.com.js.base.model.StatusOrder;
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
	  if(os.getOrderItens() == null || os.getOrderItens().isEmpty()) {
	    throw new BusinessException("Ordem de Serviço sem itens");
	  }
	  
    os.setDateIn(OffsetDateTime.now());
    os.setStatus(StatusOrder.OPEN);
	  
		return repository.save(os);
	}

  public void deleteById(Long id) {
    if(!repository.existsById(id)) {
      throw new BusinessException("Ordem de Serviço inexistente");
    }
    
    repository.deleteById(id);
  }

  public Order update(Order order) {
    var optional = repository.findById(order.getId());
    var savedOrder = optional.orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não existe"));
    BeanUtils.copyProperties(order, savedOrder);
    return repository.save(savedOrder);
  }

  public Order findByNumberAndYear(Integer number, Integer year) {
    var optional = repository.findByNumberAndYear(number, year);
    return optional.orElseThrow(() -> new BusinessException("Número e ano devem ser preenchidos"));
  }
}
