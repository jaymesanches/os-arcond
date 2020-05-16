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

import br.com.js.base.dto.ProdutoDTO;
import br.com.js.base.event.RecursoCriadoEvent;
import br.com.js.base.model.Produto;
import br.com.js.base.service.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoResource {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ProdutoService produtoService;

	@GetMapping
	public ResponseEntity<List<ProdutoDTO>> buscarPorDescricao(
			@RequestParam(required = false, defaultValue = "%") String descricao) {
		var produtos = produtoService.findByDescricaoIgnoreCaseContaining(descricao);
		return ResponseEntity.ok(toListDTO(produtos));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProdutoDTO> buscarPeloCodigo(@PathVariable Long id) {
		var produto = produtoService.findById(id);

		if (produto == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(toDTO(produto));
		}
	}

	@PostMapping
	public ResponseEntity<ProdutoDTO> salvar(@Valid @RequestBody ProdutoDTO produtoDTO, HttpServletResponse response) {
		var produto = toEntity(produtoDTO);
		var produtoSalvo = produtoService.save(produto);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, produtoSalvo.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(produtoSalvo));
	}

	@PutMapping
	public ResponseEntity<ProdutoDTO> alterar(@Valid @RequestBody ProdutoDTO produtoDTO, HttpServletResponse response) {
		var produto = toEntity(produtoDTO);
		var produtoAlterado = produtoService.update(produto);
		var dto = toDTO(produtoAlterado);
		return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		produtoService.delete(id);
	}

	private ProdutoDTO toDTO(Produto produto) {
		return modelMapper.map(produto, ProdutoDTO.class);
	}

	private List<ProdutoDTO> toListDTO(List<Produto> produtos) {
		return produtos.stream().map(produto -> toDTO(produto)).collect(Collectors.toList());
	}

	private Produto toEntity(ProdutoDTO produtoDTO) {
		return modelMapper.map(produtoDTO, Produto.class);
	}
}
