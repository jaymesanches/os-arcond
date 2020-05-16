package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Cliente;
import br.com.js.base.repository.ClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}

	public Cliente findById(Long id) {
		Optional<Cliente> clienteOptional = clienteRepository.findById(id);
		return clienteOptional.orElse(null);
	}

	public List<Cliente> findByNomeIgnoreCaseContaining(String nome) {
		return clienteRepository.findByNomeIgnoreCaseContaining(nome);
	}

	public Cliente save(Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	public void delete(Long id) {
		if (!clienteRepository.existsById(id)) {
			throw new BusinessException("Cliente inexistente");
		}

		clienteRepository.deleteById(id);
	}

	public Cliente update(Cliente cliente) {
		var clienteOptional = clienteRepository.findById(cliente.getId());
		var clienteSalvo = clienteOptional.orElseThrow(() -> new ResourceNotFoundException("Cliente não existe"));
		BeanUtils.copyProperties(cliente, clienteSalvo);
		return clienteRepository.save(clienteSalvo);
	}
}
