package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Client;
import br.com.js.base.repository.ClientRepository;

@Service
public class ClientService {

	@Autowired
	private ClientRepository repository;

	public List<Client> findAll() {
		return repository.findAll();
	}

	public Client findById(Long id) {
		Optional<Client> clientOptional = repository.findById(id);
		return clientOptional.orElse(null);
	}

	public List<Client> findByNameIgnoreCaseContaining(String name) {
		return repository.findByNameIgnoreCaseContaining(name);
	}

	public Client save(Client client) {
		return repository.save(client);
	}

	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new BusinessException("Cliente inexistente");
		}

		repository.deleteById(id);
	}

	public Client update(Client client) {
		var clientOptional = repository.findById(client.getId());
		var savedClient = clientOptional.orElseThrow(() -> new ResourceNotFoundException("Cliente não existe"));
		BeanUtils.copyProperties(client, savedClient);
		return repository.save(savedClient);
	}
}
