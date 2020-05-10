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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.js.base.dto.ServicoDTO;
import br.com.js.base.event.RecursoCriadoEvent;
import br.com.js.base.model.Servico;
import br.com.js.base.service.CadastroServicoService;

@RestController
@RequestMapping("/servicos")
public class ServicoResource {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CadastroServicoService cadastroServicoService;

	@GetMapping
	public List<ServicoDTO> listar() {
		var clientes = cadastroServicoService.findAll();
		return toServicosDTO(clientes);
	}

	@PostMapping
	public ResponseEntity<ServicoDTO> salvar(@Valid @RequestBody ServicoDTO servicoDTO, HttpServletResponse response) {
		var servico = toEntity(servicoDTO);
		var servicoSalvo = cadastroServicoService.save(servico);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, servicoSalvo.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(toServicoDTO(servicoSalvo));
	}

	@PutMapping
	public ResponseEntity<ServicoDTO> alterar(@Valid @RequestBody ServicoDTO servicoDTO, HttpServletResponse response) {
		var servicoAlterado = cadastroServicoService.save(toEntity(servicoDTO));
		return ResponseEntity.ok(toServicoDTO(servicoAlterado));
	}

	private ServicoDTO toServicoDTO(Servico servico) {
		return modelMapper.map(servico, ServicoDTO.class);
	}

	private List<ServicoDTO> toServicosDTO(List<Servico> servicos) {
		return servicos.stream().map(servico -> toServicoDTO(servico)).collect(Collectors.toList());
	}

	private Servico toEntity(ServicoDTO servicoDTO) {
		return modelMapper.map(servicoDTO, Servico.class);
	}
}
