package br.com.js.base.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Work;
import br.com.js.base.repository.WorkRepository;

@Service
public class WorkService {

	@Autowired
	private WorkRepository repository;

	public List<Work> findAll() {
		return repository.findAll();
	}

	public Work findById(Long id) {
		var optional = repository.findById(id);
		return optional.orElse(null);
	}

	public List<Work> findByNameIgnoreCaseContaining(String name) {
		if (name == null) {
			throw new BusinessException("Nome precisa ser preenchido");
		}

		return repository.findByNameIgnoreCaseContaining(name);
	}

	public Work save(Work work) {
		return repository.save(work);
	}

	public Work update(Work work) {
		var optional = repository.findById(work.getId());
		var savedWork = optional.orElseThrow(() -> new ResourceNotFoundException("Serviço não existe"));
		BeanUtils.copyProperties(work, savedWork);
		return repository.save(savedWork);
	}

	public void delete(Long id) {
		if (!repository.existsById(id)) {
			throw new BusinessException("Serviço inexistente");
		}

		repository.deleteById(id);
	}
}
