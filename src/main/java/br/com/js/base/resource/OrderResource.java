package br.com.js.base.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.js.base.dto.OrderDTO;
import br.com.js.base.event.CreatedResourceEvent;
import br.com.js.base.model.Order;
import br.com.js.base.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderResource {
	
  @Autowired
  private OrderService service;
  
  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  private ModelMapper modelMapper;

  @GetMapping
  public ResponseEntity<List<OrderDTO>> findAll() {
    var orders = service.findAll();

    if (orders == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(toListDTO(orders));
    }
  }
  
  @GetMapping("/{id}")
  public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
    var client = service.findById(id);

    if (client == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(toDTO(client));
    }
  }
  
  @PostMapping
  public ResponseEntity<OrderDTO> save(@Valid @RequestBody OrderDTO orderDTO, HttpServletResponse response) {
    var product = toEntity(orderDTO);
    var savedProduct = service.save(product);
    publisher.publishEvent(new CreatedResourceEvent(this, response, savedProduct.getId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedProduct));
  }
  
  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable Long id) {
    service.deleteById(id);
  }
  
  @PutMapping()
  public ResponseEntity<OrderDTO> update(@Valid @RequestBody OrderDTO orderDTO, HttpServletResponse response) {
    var order = toEntity(orderDTO);
    var updatedOrder = service.update(order);
    var dto = toDTO(updatedOrder);
    return ResponseEntity.ok(dto);
  }
  
  private OrderDTO toDTO(Order order) {
    return modelMapper.map(order, OrderDTO.class);
  }

  private List<OrderDTO> toListDTO(List<Order> clients) {
    return clients.stream().map(client -> toDTO(client)).collect(Collectors.toList());
  }

  private Order toEntity(OrderDTO clientDTO) {
    return modelMapper.map(clientDTO, Order.class);
  }
}
