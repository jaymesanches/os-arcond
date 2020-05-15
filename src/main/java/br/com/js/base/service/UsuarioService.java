package br.com.js.base.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

	public List<Usuario> findByNome(String nome) {
		if (nome == null) {
			throw new BusinessException("Nome precisa ser preenchido");
		}

		return usuarioRepository.findByNomeIgnoringCaseContaining(nome);
	}

	public Usuario save(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

}
