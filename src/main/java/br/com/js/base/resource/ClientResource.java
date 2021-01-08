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

import br.com.js.base.dto.ClientDTO;
import br.com.js.base.event.CreatedResourceEvent;
import br.com.js.base.model.Client;
import br.com.js.base.service.ClientService;

@RestController
@RequestMapping("/clients")
public class ClientResource {

	@Autowired
	private ClientService service;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/{id}")
	public ResponseEntity<ClientDTO> findById(@PathVariable Long id) {
		var client = service.findById(id);

		if (client == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(toDTO(client));
		}
	}

	@GetMapping
	public ResponseEntity<List<ClientDTO>> findByName(
			@RequestParam(required = false, defaultValue = "") String name) {
		var clients = service.findByNameIgnoreCaseContaining(name);
		return ResponseEntity.ok(toListDTO(clients));
	}

	@PostMapping
	public ResponseEntity<Client> save(@Valid @RequestBody ClientDTO clientDTO, HttpServletResponse response) {
		var client = toEntity(clientDTO);
//		cliente.setDataCadastro(OffsetDateTime.now());
		var savedClient = service.save(client);
		publisher.publishEvent(new CreatedResourceEvent(this, response, savedClient.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
	}

	@PutMapping()
	public ResponseEntity<ClientDTO> update(@Valid @RequestBody ClientDTO clientDTO, HttpServletResponse response) {
		var client = toEntity(clientDTO);
		var updatedClient = service.update(client);
		var dto = toDTO(updatedClient);
		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		if (service.findById(id) == null) {
			throw new ResourceNotFoundException();
		}
		service.delete(id);
	}

	private ClientDTO toDTO(Client client) {
		return modelMapper.map(client, ClientDTO.class);
	}

	private List<ClientDTO> toListDTO(List<Client> clients) {
		return clients.stream().map(client -> toDTO(client)).collect(Collectors.toList());
	}

	private Client toEntity(ClientDTO clientDTO) {
		return modelMapper.map(clientDTO, Client.class);
	}
}
