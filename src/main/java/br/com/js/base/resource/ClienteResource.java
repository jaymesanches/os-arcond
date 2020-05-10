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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.js.base.dto.ClienteDTO;
import br.com.js.base.dto.EnderecoDTO;
import br.com.js.base.event.RecursoCriadoEvent;
import br.com.js.base.model.Cliente;
import br.com.js.base.model.Endereco;
import br.com.js.base.service.CadastroClienteService;

@RestController
@RequestMapping("/clientes")
public class ClienteResource {

	@Autowired
	private CadastroClienteService cadastroClienteService;

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping
	public List<ClienteDTO> listar() {
		var clientes = cadastroClienteService.findAll();
		return toClientesDTO(clientes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClienteDTO> buscarPeloCodigo(@PathVariable Long id) {
		var cliente = cadastroClienteService.findById(id);

		if (cliente == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(toClienteDTO(cliente));
		}
	}

	@GetMapping("/search")
	public ResponseEntity<List<ClienteDTO>> buscarPeloNome(
			@RequestParam(required = false, defaultValue = "%") String nome) {
		var clientes = cadastroClienteService.findByNome(nome);
		return ResponseEntity.ok(toClientesDTO(clientes));
	}

	@PostMapping
	public ResponseEntity<Cliente> salvar(@Valid @RequestBody ClienteDTO clienteDTO, HttpServletResponse response) {
		var cliente = toEntity(clienteDTO);
//		cliente.setDataCadastro(OffsetDateTime.now());
		var clienteSalvo = cadastroClienteService.save(cliente);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, clienteSalvo.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
	}

	@PutMapping
	public ResponseEntity<Cliente> alterar(@Valid @RequestBody ClienteDTO clienteDTO, HttpServletResponse response) {
		var clienteAlterado = cadastroClienteService.save(toEntity(clienteDTO));
		return ResponseEntity.ok(clienteAlterado);
	}

	@GetMapping("/{idCliente}/enderecos")
	public List<EnderecoDTO> pesquisarEnderecos(@PathVariable Long idCliente){
		var cliente = cadastroClienteService.findById(idCliente);
		return toEnderecosDTO(cliente.getEnderecos());
	}

	private ClienteDTO toClienteDTO(Cliente cliente) {
		return modelMapper.map(cliente, ClienteDTO.class);
	}

	private List<ClienteDTO> toClientesDTO(List<Cliente> clientes) {
		return clientes.stream().map(cliente -> toClienteDTO(cliente)).collect(Collectors.toList());
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
