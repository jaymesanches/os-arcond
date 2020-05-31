package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.helper.ProductTestHelper;
import br.com.js.base.model.Product;
import br.com.js.base.repository.ProductRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  ProductService service;

  @MockBean
  ProductRepository repository;

  @Test
  @DisplayName("Deve pesquisar todos os produtos")
  public void Should_ReturnList_FindAllProducts() throws Exception {
    var products = ProductTestHelper.getProductList();

    when(repository.findAll()).thenReturn(products);

    var list = service.findAll();

    assertThat(list).isNotEmpty();
    assertThat(list.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("Deve retornar um produto pelo id")
  public void Shoul_ReturnProduct_When_FindProductById() throws Exception {
    var optional = Optional.of(ProductTestHelper.getProduct());

    when(repository.findById(anyLong())).thenReturn((optional));

    var product = service.findById(1l);

    assertThat(product).isNotNull();
  }

  @Test
  @DisplayName("Deve retornar nulo quando pesquisar por id inválido")
  public void Should_ReturnNull_When_FindProductByInvalidId() throws Exception {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    var product = service.findById(1l);

    assertThat(product).isNull();
  }

  @Test
  @DisplayName("Deve retornar uma lista de produtos ao pesquisa por parte da descrição")
  public void Should_ReturnList_When_FindProductsByName() throws Exception {
    var products = ProductTestHelper.getProductList();
    when(repository.findByNameIgnoreCaseContaining(anyString())).thenReturn(products);

    var list = service.findByNameIgnoreCaseContaining("filtro");

    assertThat(list).isNotEmpty();
  }

  @Test
  @DisplayName("Deve retornar erro ao pesquisar por nome sem nome")
  public void Should_ThrowException_When_FindProductsByNameWithoutName() throws Exception {
    Throwable exception = Assertions.catchThrowable(() -> service.findByNameIgnoreCaseContaining(null));

    assertThat(exception).isInstanceOf(BusinessException.class);
    assertThat(exception).hasFieldOrPropertyWithValue("message", "Nome precisa ser preenchido");
  }

  @Test
  @DisplayName("Deve salvar um produto")
  public void Should_ReturnProduct_When_SaveProduct() {
    var product = ProductTestHelper.getProduct();

    when(repository.save(product)).thenReturn(ProductTestHelper.getProduct(1l));

    var savedProduct = service.save(product);

    assertThat(savedProduct.getId()).isNotNull();
    assertThat(savedProduct.getName()).asString().contains("Filtro");

    // @formatter:on
  }

  @Test
  @DisplayName("Deve remover um produto")
  public void Should_DeleteProduct() throws Exception {
    when(repository.existsById(anyLong())).thenReturn(true);

    service.delete(123l);

    verify(repository, atLeastOnce()).deleteById(anyLong());
  }

  @Test
  @DisplayName("Deve retornar exceção ao tentar remover um produto com id inválido")
  public void Should_ThrowException_When_DeleteInvalidProduct() throws Exception {
    doNothing().when(repository).deleteById(anyLong());

    Throwable exception = Assertions.catchThrowable(() -> service.delete(1l));

    assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("message",
        "Produto inexistente");
  }

  @Test
  @DisplayName("Deve retornar erro ao criar um produto com dados incompletos")
  public void Should_ThrowException_When_SaveProductWithoutName() {
    var product = ProductTestHelper.getProduct();
    product.setName(null);

    when(repository.save(product)).thenThrow(DataIntegrityViolationException.class);

    Throwable exception = Assertions.catchThrowable(() -> service.save(product));

    assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("Deve alterar um produto")
  public void Should_ReturnUpdatedProduct_When_UpdateProduct() throws Exception {
    var product = ProductTestHelper.getProduct();
    product.setId(1l);

    when(repository.findById(1l)).thenReturn(Optional.of(product));
    when(repository.save(Mockito.any(Product.class))).thenReturn(product);

    var savedProduct = service.update(product);

    assertThat(savedProduct.getName().equals(product.getName()));
    verify(repository, Mockito.atLeastOnce()).save(product);
  }

  @Test
  @DisplayName("Deve retornar erro ao alterar um produto não existente")
  public void Should_ThrowException_When_UpdateInvalidProduct() throws Exception {
    var product = ProductTestHelper.getProduct();

    when(repository.findById(anyLong())).thenThrow(new ResourceNotFoundException("Cliente não existe"));

    Throwable exception = Assertions.catchThrowable(() -> service.update(product));

    assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    verify(repository, never()).save(product);
  }
}
