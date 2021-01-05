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

import br.com.js.base.dto.ProductDTO;
import br.com.js.base.event.CreatedResourceEvent;
import br.com.js.base.model.Product;
import br.com.js.base.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductResource {

	@Autowired
	private ApplicationEventPublisher publisher;

	@Autowired
	private ModelMapper modelMapper;
	
	 @Autowired
	private ProductService service;
	
	@GetMapping
	public ResponseEntity<List<ProductDTO>> findByName(
			@RequestParam(required = false, defaultValue = "") String name) {
		var products = service.findByNameIgnoreCaseContaining(name);
		return ResponseEntity.ok(toListDTO(products));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ProductDTO> findbyId(@PathVariable Long id) {
		var product = service.findById(id);

		if (product == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok(toDTO(product));
		}
	}

	@PostMapping
	public ResponseEntity<ProductDTO> save(@Valid @RequestBody ProductDTO productDTO, HttpServletResponse response) {
		var product = toEntity(productDTO);
		var savedProduct = service.save(product);
		publisher.publishEvent(new CreatedResourceEvent(this, response, savedProduct.getId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedProduct));
	}

	@PutMapping
	public ResponseEntity<ProductDTO> update(@Valid @RequestBody ProductDTO productDTO, HttpServletResponse response) {
		var product = toEntity(productDTO);
		var savedProduct = service.update(product);
		var dto = toDTO(savedProduct);
		return ResponseEntity.ok(dto);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	private ProductDTO toDTO(Product product) {
		var dto = modelMapper.map(product, ProductDTO.class);
		return dto;
	}

	private List<ProductDTO> toListDTO(List<Product> products) {
		return products.stream().map(product -> toDTO(product)).collect(Collectors.toList());
	}

	private Product toEntity(ProductDTO productDTO) {
		var product=  modelMapper.map(productDTO, Product.class);
		return product;
	}
}
