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

import br.com.js.base.dto.WorkDTO;
import br.com.js.base.event.CreatedResourceEvent;
import br.com.js.base.model.Work;
import br.com.js.base.service.WorkService;

@RestController
@RequestMapping("/works")
public class WorkResource {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private WorkService service;

	@GetMapping
	public ResponseEntity<List<WorkDTO>> findByName(
			@RequestParam(required = false, defaultValue = "") String name) {
		var works = service.findByNameIgnoreCaseContaining(name);
		return ResponseEntity.ok(toListDTO(works));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<WorkDTO> findById(@PathVariable Long id) {
		var work = service.findById(id);

		if (work == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(toDTO(work));
		}
	}

	@PostMapping
	public ResponseEntity<WorkDTO> save(@Valid @RequestBody WorkDTO workDTO, HttpServletResponse response) {
		var work = toEntity(workDTO);
		var savedWork = service.save(work);
		publisher.publishEvent(new CreatedResourceEvent(this, response, savedWork.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedWork));
	}

	@PutMapping
	public ResponseEntity<WorkDTO> update(@Valid @RequestBody WorkDTO workDTO, HttpServletResponse response) {
		var work = toEntity(workDTO);
		var updatedWork = service.update(work);
		var dto = toDTO(updatedWork);
		return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	private WorkDTO toDTO(Work work) {
		return modelMapper.map(work, WorkDTO.class);
	}

	private List<WorkDTO> toListDTO(List<Work> works) {
		return works.stream().map(work -> toDTO(work)).collect(Collectors.toList());
	}

	private Work toEntity(WorkDTO workDTO) {
		return modelMapper.map(workDTO, Work.class);
	}
}
