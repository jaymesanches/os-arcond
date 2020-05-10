package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.js.base.model.OrdemServico;
import br.com.js.base.repository.OrdemServicoRepository;

@Service
public class OrdemServicoService {

	@Autowired
	private OrdemServicoRepository ordemServicoRepository;

	public List<OrdemServico> listar() {
		return ordemServicoRepository.findAll();
	}
	
	public OrdemServico findById(Long id) {
		Optional<OrdemServico> ordemServicoOptional = ordemServicoRepository.findById(id);
		return ordemServicoOptional.orElse(null);
	}

	public OrdemServico save(OrdemServico os) {
		return ordemServicoRepository.save(os);
	}
}
