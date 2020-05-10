package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.js.base.model.Servico;
import br.com.js.base.repository.ServicoRepository;

@Service
public class CadastroServicoService {

	@Autowired
	private ServicoRepository servicoRepository;

	public List<Servico> findAll() {
		return servicoRepository.findAll();
	}
	
	public Optional<Servico> findById(Long id) {
		return servicoRepository.findById(id);
	}

	public Servico save(Servico servico) {
		return servicoRepository.save(servico);
	}
}
