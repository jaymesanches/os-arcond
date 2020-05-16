package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Usuario;
import br.com.js.base.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public List<Usuario> listar() {
		return usuarioRepository.findAll();
	}

	public Usuario findById(Long id) {
		Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
		return usuarioOptional.orElse(null);
	}

	public List<Usuario> findByNomeIgnoreCaseContaining(String nome) {
		if (nome == null) {
			throw new BusinessException("Nome precisa ser preenchido");
		}

		return usuarioRepository.findByNomeIgnoreCaseContaining(nome);
	}

	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Usuario update(Usuario usuario) {
		var usuarioOptional = usuarioRepository.findById(usuario.getId());
		var usuarioSalvo = usuarioOptional.orElseThrow(() -> new ResourceNotFoundException("Usuário não existe"));
		BeanUtils.copyProperties(usuario, usuarioSalvo);
		return usuarioRepository.save(usuarioSalvo);
	}

	public void delete(long id) {
		if (!usuarioRepository.existsById(id)) {
			throw new BusinessException("Usuário inexistente");
		}

		usuarioRepository.deleteById(id);
	}

	public List<Usuario> findAll() {
		return usuarioRepository.findAll();
	}
}
