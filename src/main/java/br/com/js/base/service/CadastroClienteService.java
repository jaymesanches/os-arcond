package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Cliente;
import br.com.js.base.repository.ClienteRepository;

@Service
public class CadastroClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	public List<Cliente> findAll() {
		return clienteRepository.findAll();
	}

	public Cliente findById(Long id) {
		Optional<Cliente> clienteOptional = clienteRepository.findById(id);
		return clienteOptional.orElse(null);
	}

	public List<Cliente> findByNome(String nome) {
		if (nome == null) {
			throw new BusinessException("Nome precisa ser preenchido");
		}

		return clienteRepository.findByNomeIgnoringCaseContaining(nome);
	}

	public Cliente save(Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	public void delete(Long id) {
		clienteRepository.deleteById(id);
	}
}
