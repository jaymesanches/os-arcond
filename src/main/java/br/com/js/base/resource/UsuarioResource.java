package br.com.js.base.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.js.base.dto.UsuarioDTO;
import br.com.js.base.event.RecursoCriadoEvent;
import br.com.js.base.model.Usuario;
import br.com.js.base.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResource {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> buscarPeloCodigo(@PathVariable Long id) {
		var usuario = usuarioService.findById(id);

		if (usuario == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(toDTO(usuario));
		}
	}

	@GetMapping
	public ResponseEntity<List<UsuarioDTO>> buscarPeloNome(
			@RequestParam(required = false, defaultValue = "%") String nome) {
		var clientes = usuarioService.findByNomeIgnoreCaseContaining(nome);
		return ResponseEntity.ok(toListDTO(clientes));
	}

	
	@PostMapping
	public ResponseEntity<UsuarioDTO> salvar(@RequestBody @Valid UsuarioDTO usuarioDTO, HttpServletResponse response) {
		var usuario = toEntity(usuarioDTO);
		Usuario usuarioSalvo = usuarioService.save(usuario);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, usuarioSalvo.getId()));
		var dto = toDTO(usuarioSalvo);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}
	
	@PutMapping()
	public ResponseEntity<UsuarioDTO> alterar(@Valid @RequestBody UsuarioDTO usuarioDTO, HttpServletResponse response) {
		var usuario = toEntity(usuarioDTO);
		var usuarioAlterado = usuarioService.update(usuario);
		var dto = toDTO(usuarioAlterado);
		return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		if (usuarioService.findById(id) == null) {
			throw new ResourceNotFoundException();
		}
		
		usuarioService.delete(id);
	}
	
	private Usuario toEntity(UsuarioDTO usuarioDTO) {
		return modelMapper.map(usuarioDTO, Usuario.class);
	}
	
	private UsuarioDTO toDTO(Usuario usuario) {
		return modelMapper.map(usuario, UsuarioDTO.class);
	}

	private List<UsuarioDTO> toListDTO(List<Usuario> usuarios) {
		return usuarios.stream().map(usuario -> toDTO(usuario)).collect(Collectors.toList());
	}
}
