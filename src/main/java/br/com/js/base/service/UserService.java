package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.User;
import br.com.js.base.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public List<User> findAll() {
		return repository.findAll();
	}

	public User findById(Long id) {
		Optional<User> optional = repository.findById(id);
		return optional.orElse(null);
	}

	public List<User> findByNameIgnoreCaseContaining(String name) {
		if (name == null) {
			throw new BusinessException("Nome precisa ser preenchido.");
		}

		return repository.findByNameIgnoreCaseContaining(name);
	}

	public User save(User usuario) {
		return repository.save(usuario);
	}

	public User update(User usuario) {
		var usuarioOptional = repository.findById(usuario.getId());
		var usuarioSalvo = usuarioOptional.orElseThrow(() -> new ResourceNotFoundException("Usuário não existe"));
		BeanUtils.copyProperties(usuario, usuarioSalvo);
		return repository.save(usuarioSalvo);
	}

	public void delete(long id) {
		if (!repository.existsById(id)) {
			throw new BusinessException("Usuário inexistente");
		}

		repository.deleteById(id);
	}

  public Page<User> find(User filter, Pageable pageRequest) {
    // @formatter:off
    var example = Example.of(filter, 
        ExampleMatcher
          .matching()
          .withIgnoreCase()
          .withIgnoreNullValues()
          .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
    
    return repository.findAll(example, pageRequest);
    // @formatter:on
  }
}
