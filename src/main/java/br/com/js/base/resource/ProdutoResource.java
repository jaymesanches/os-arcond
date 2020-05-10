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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.js.base.dto.ProdutoDTO;
import br.com.js.base.event.RecursoCriadoEvent;
import br.com.js.base.model.Produto;
import br.com.js.base.service.CadastroProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoResource {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CadastroProdutoService cadastroProdutoService;

	@GetMapping
	public List<ProdutoDTO> listar() {
		var produtos = cadastroProdutoService.findAll();
		return toProdutosDTO(produtos);
	}

	@PostMapping
	public ResponseEntity<ProdutoDTO> salvar(@Valid @RequestBody ProdutoDTO produtoDTO, HttpServletResponse response) {
		var produto = toEntity(produtoDTO);
		var produtoSalvo = cadastroProdutoService.save(produto);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, produtoSalvo.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(toProdutoDTO(produtoSalvo));
	}

	@PutMapping
	public ResponseEntity<ProdutoDTO> alterar(@Valid @RequestBody ProdutoDTO produtoDTO, HttpServletResponse response) {
		var produtoAlterado = cadastroProdutoService.save(toEntity(produtoDTO));
		return ResponseEntity.ok(toProdutoDTO(produtoAlterado));
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		cadastroProdutoService.delete(id);
	}

	private ProdutoDTO toProdutoDTO(Produto produto) {
		return modelMapper.map(produto, ProdutoDTO.class);
	}

	private List<ProdutoDTO> toProdutosDTO(List<Produto> produtos) {
		return produtos.stream().map(produto -> toProdutoDTO(produto)).collect(Collectors.toList());
	}

	private Produto toEntity(ProdutoDTO produtoDTO) {
		return modelMapper.map(produtoDTO, Produto.class);
	}
}
