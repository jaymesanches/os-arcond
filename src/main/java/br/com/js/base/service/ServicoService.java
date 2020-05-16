package br.com.js.base.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Servico;
import br.com.js.base.repository.ServicoRepository;

@Service
public class ServicoService {

	@Autowired
	private ServicoRepository servicoRepository;

	public List<Servico> findAll() {
		return servicoRepository.findAll();
	}

	public Servico findById(Long id) {
		var optional = servicoRepository.findById(id);
		return optional.orElse(null);
	}

	public List<Servico> findByDescricaoIgnoreCaseContaining(String descricao) {
		if (descricao == null) {
			throw new BusinessException("Descrição precisa ser preenchido");
		}

		return servicoRepository.findByDescricaoIgnoreCaseContaining(descricao);
	}

	public Servico save(Servico servico) {
		return servicoRepository.save(servico);
	}

	public Servico update(Servico servico) {
		var servicoOptional = servicoRepository.findById(servico.getId());
		var servicoSalvo = servicoOptional.orElseThrow(() -> new ResourceNotFoundException("Serviço não existe"));
		BeanUtils.copyProperties(servico, servicoSalvo);
		return servicoRepository.save(servicoSalvo);
	}

	public void delete(Long id) {
		if (!servicoRepository.existsById(id)) {
			throw new BusinessException("Serviço inexistente");
		}

		servicoRepository.deleteById(id);
	}

}
