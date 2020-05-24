package br.com.js.base.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.js.base.helper.ProductTestHelper;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ProductRepositoryTest {

	@Autowired
	TestEntityManager entityManager;

	@Autowired
	ProductRepository repository;

	@Test
	@DisplayName("Deve salvar um produto")
	public void Should_SaveProduct() throws Exception {
		var savedProduct = repository.save(ProductTestHelper.getProduct());
		assertThat(savedProduct.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao salvar um produto com dados incompletos")
	public void Should_ThrowException_When_SaveProductWithInvalidData() throws Exception {
		var product = ProductTestHelper.getProduct();
		product.setName(null);
		
		var exception = Assertions.catchThrowable(() -> repository.save(product));
		
		assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve deletar um produto")
	public void Should_DeleteProduct() throws Exception {
		var product = entityManager.persist(ProductTestHelper.getProduct());
		var exception = Assertions.catchThrowable(() -> repository.deleteById(product.getId()));
		assertThat(exception).isNull();
	}
	
	@Test
	@DisplayName("Deve retornar erro ao deletar um produto inexistente")
	public void Should_ThrowException_WhenDelete_InvalidProduct() throws Exception {
		var invalidId = 987654321l;
		var exception = Assertions.catchThrowable(() -> repository.deleteById(invalidId));
		assertThat(exception).isInstanceOf(EmptyResultDataAccessException.class);
	}

	@Test
	@DisplayName("Deve retornar true se existir produto com a descrição informada")
	public void Should_ReturnTrue_IfExistsProductWithName() {
		var product = ProductTestHelper.getProduct();
		var savedProduct = entityManager.persist(product);

		boolean exists = repository.existsByNameIgnoreCaseContaining("Filtro");

		assertThat(exists).isTrue();
		assertThat(savedProduct.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve retornar nulo se não existir produto com a descrição informada")
	public void Should_ReturnNull_IfProductNotExists() {
		boolean exists = repository.existsByNameIgnoreCaseContaining("Filtro");

		assertThat(exists).isFalse();
	}
}
