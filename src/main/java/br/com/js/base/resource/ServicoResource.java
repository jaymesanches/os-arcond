package br.com.js.base.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

import br.com.js.base.dto.ServicoDTO;
import br.com.js.base.event.RecursoCriadoEvent;
import br.com.js.base.model.Servico;
import br.com.js.base.service.ServicoService;

@RestController
@RequestMapping("/servicos")
public class ServicoResource {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ServicoService servicoService;

	@GetMapping
	public ResponseEntity<List<ServicoDTO>> buscarPorDescricao(
			@RequestParam(required = false, defaultValue = "%") String descricao) {
		var servicos = servicoService.findByDescricaoIgnoreCaseContaining(descricao);
		return ResponseEntity.ok(toListDTO(servicos));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ServicoDTO> buscarPeloCodigo(@PathVariable Long id) {
		var servico = servicoService.findById(id);

		if (servico == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(toDTO(servico));
		}
	}

	@PostMapping
	public ResponseEntity<ServicoDTO> salvar(@Valid @RequestBody ServicoDTO servicoDTO, HttpServletResponse response) {
		var servico = toEntity(servicoDTO);
		var servicoSalvo = servicoService.save(servico);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, servicoSalvo.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(servicoSalvo));
	}

	@PutMapping
	public ResponseEntity<ServicoDTO> alterar(@Valid @RequestBody ServicoDTO servicoDTO, HttpServletResponse response) {
		var servico = toEntity(servicoDTO);
		var servicoAlterado = servicoService.update(servico);
		var dto = toDTO(servicoAlterado);
		return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		servicoService.delete(id);
	}

	private ServicoDTO toDTO(Servico servico) {
		return modelMapper.map(servico, ServicoDTO.class);
	}

	private List<ServicoDTO> toListDTO(List<Servico> servicos) {
		return servicos.stream().map(servico -> toDTO(servico)).collect(Collectors.toList());
	}

	private Servico toEntity(ServicoDTO servicoDTO) {
		return modelMapper.map(servicoDTO, Servico.class);
	}
}
