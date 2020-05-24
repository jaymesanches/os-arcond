package br.com.js.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.js.base.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
	
}
