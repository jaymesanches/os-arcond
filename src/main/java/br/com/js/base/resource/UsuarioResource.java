package br.com.js.base.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.js.base.dto.UsuarioDTO;
import br.com.js.base.event.RecursoCriadoEvent;
import br.com.js.base.model.Usuario;
import br.com.js.base.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {
	
	@Autowired
	private UsuarioService cadastroUsuarioService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@GetMapping
	public List<UsuarioDTO> listar() {
		var usuarios = cadastroUsuarioService.listar();
		return toListDTO(usuarios);
	}
	
	@PostMapping
	public ResponseEntity<UsuarioDTO> salvar(@RequestBody Usuario usuario, HttpServletResponse response) {
		Usuario usuarioSalvo = cadastroUsuarioService.save(usuario);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, usuarioSalvo.getId()));
		var usuarioDTO = toDTO(usuarioSalvo);
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
	}
	
	private UsuarioDTO toDTO(Usuario usuario) {
		return modelMapper.map(usuario, UsuarioDTO.class);
	}

	private List<UsuarioDTO> toListDTO(List<Usuario> usuarios) {
		return usuarios.stream().map(usuario -> toDTO(usuario)).collect(Collectors.toList());
	}
}
