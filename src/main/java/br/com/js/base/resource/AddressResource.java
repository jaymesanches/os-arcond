package br.com.js.base.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.js.base.dto.AddressDTO;
import br.com.js.base.event.CreatedResourceEvent;
import br.com.js.base.model.Address;
import br.com.js.base.repository.AddressRepository;

@RestController
@RequestMapping("/addresses")
public class AddressResource {

  @Autowired
  private AddressRepository repository;

  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  private ModelMapper modelMapper;

  @GetMapping
  public List<Address> findAll() {
    return repository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Address> findById(@PathVariable Long id) {
    Optional<Address> optional = repository.findById(id);
    return optional.isPresent() ? ResponseEntity.ok(optional.get()) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<AddressDTO> save(@RequestBody AddressDTO addressDTO, HttpServletResponse response) {
    Address savedAddress = repository.save(toEntity(addressDTO));
    publisher.publishEvent(new CreatedResourceEvent(this, response, savedAddress.getId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toAddressDTO(savedAddress));
  }

  private AddressDTO toAddressDTO(Address address) {
    return modelMapper.map(address, AddressDTO.class);
  }

  private Address toEntity(AddressDTO enderecoDTO) {
    return modelMapper.map(enderecoDTO, Address.class);
  }
}
