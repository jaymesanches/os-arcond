package br.com.js.base.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Product;
import br.com.js.base.repository.ProductRepository;

@Service
public class ProductService {

  @Autowired
  private ProductRepository repository;
  
  public List<Product> findAll() {
    return repository.findAll();
  }

  public Product findById(Long id) {
    var optional = repository.findById(id);
    return optional.orElse(null);
  }

  public List<Product> findByNameIgnoreCaseContaining(String name) {
    if (name == null) {
      throw new BusinessException("Nome precisa ser preenchido");
    }

    return repository.findByNameIgnoreCaseContaining(name);
  }

  public Product save(Product product) {
    return repository.save(product);
  }

  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new BusinessException("Produto inexistente");
    }

    repository.deleteById(id);
  }

  public Product update(Product product) {
    var optional = repository.findById(product.getId());
    var savedProduct = optional.orElseThrow(() -> new ResourceNotFoundException("Produto não existe"));
    BeanUtils.copyProperties(product, savedProduct);
    return repository.save(savedProduct);
  }
}
