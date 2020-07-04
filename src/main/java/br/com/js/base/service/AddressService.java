package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.js.base.model.Address;
import br.com.js.base.repository.AddressRepository;

@Service
public class AddressService {

  @Autowired
  private AddressRepository repository;

  public List<Address> findAll() {
    return repository.findAll();
  }

  public Address save(Address address) {
    return repository.save(address);
  }

  public Optional<Address> findById(long id) {
    return repository.findById(id);
  }

}
