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

import br.com.js.base.dto.ClienteDTO;
import br.com.js.base.dto.EnderecoDTO;
import br.com.js.base.event.RecursoCriadoEvent;
import br.com.js.base.model.Cliente;
import br.com.js.base.model.Endereco;
import br.com.js.base.service.ClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteResource {

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping("/{id}")
	public ResponseEntity<ClienteDTO> buscarPeloCodigo(@PathVariable Long id) {
		var cliente = clienteService.findById(id);

		if (cliente == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(toDTO(cliente));
		}
	}

	@GetMapping
	public ResponseEntity<List<ClienteDTO>> buscarPeloNome(
			@RequestParam(required = false, defaultValue = "%") String nome) {
		var clientes = clienteService.findByNomeIgnoreCaseContaining(nome);
		return ResponseEntity.ok(toListDTO(clientes));
	}

	@PostMapping
	public ResponseEntity<Cliente> salvar(@Valid @RequestBody ClienteDTO clienteDTO, HttpServletResponse response) {
		var cliente = toEntity(clienteDTO);
//		cliente.setDataCadastro(OffsetDateTime.now());
		var clienteSalvo = clienteService.save(cliente);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, clienteSalvo.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
	}

	@PutMapping()
	public ResponseEntity<ClienteDTO> alterar(@Valid @RequestBody ClienteDTO clienteDTO, HttpServletResponse response) {
		var cliente = toEntity(clienteDTO);
		var clienteAlterado = clienteService.update(cliente);
		var dto = toDTO(clienteAlterado);
		return ResponseEntity.ok(dto);
	}

	@GetMapping("/{idCliente}/enderecos")
	public List<EnderecoDTO> pesquisarEnderecos(@PathVariable Long idCliente){
		var cliente = clienteService.findById(idCliente);
		return toEnderecosDTO(cliente.getEnderecos());
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		if (clienteService.findById(id) == null) {
			throw new ResourceNotFoundException();
		}
		clienteService.delete(id);
	}

	private ClienteDTO toDTO(Cliente cliente) {
		return modelMapper.map(cliente, ClienteDTO.class);
	}

	private List<ClienteDTO> toListDTO(List<Cliente> clientes) {
		return clientes.stream().map(cliente -> toDTO(cliente)).collect(Collectors.toList());
	}

	private Cliente toEntity(ClienteDTO clienteDTO) {
		return modelMapper.map(clienteDTO, Cliente.class);
	}

	@SuppressWarnings("unused")
	private List<Cliente> toListEntity(List<ClienteDTO> lista) {
		return lista.stream().map(dto -> toEntity(dto)).collect(Collectors.toList());
	}

	private List<EnderecoDTO> toEnderecosDTO(List<Endereco> enderecos) {
		return enderecos.stream().map(dto -> toEnderecoDTO(dto)).collect(Collectors.toList());
	}

	private EnderecoDTO toEnderecoDTO(Endereco endereco) {
		return modelMapper.map(endereco, EnderecoDTO.class);
	}
}
