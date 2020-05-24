package br.com.js.base.helper;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.js.base.dto.ProductDTO;
import br.com.js.base.model.Product;

public class ProductTestHelper {

	private static final String FILTRO_DE_AR = "Filtro de ar";

  public static Product getProduct() {
		return getProduct(null);
	}

	public static Product getProduct(Long id) {
		// @formatter:off
		return Product.builder()
			.id(id)
			.name(FILTRO_DE_AR)
			.sku("100")
			.stock(10)
			.costPrice(BigDecimal.ONE)
			.salePrice(BigDecimal.TEN)
			.build();
		// @formatter:on
	}

	public static ProductDTO getProductDTO() {
		return getProductDTO(null);
	}

	public static ProductDTO getProductDTO(Long id) {
		// @formatter:off
		return ProductDTO.builder()
		    .id(id)
	      .name(FILTRO_DE_AR)
	      .sku("100")
	      .stock(10)
	      .costPrice(BigDecimal.ONE)
	      .salePrice(BigDecimal.TEN)
	      .build();
		// @formatter:on
	}

	public static ArrayList<Product> getProductList() {
		// @formatter:off
		var product1 = Product.builder()
			.sku("F123")
			.name(FILTRO_DE_AR)
      .stock(10)
      .costPrice(BigDecimal.ONE)
      .salePrice(BigDecimal.TEN)
			.build();

		var product2 = Product.builder()
			.sku("F321")
			.name("Filtro de óleo")
      .stock(10)
      .costPrice(BigDecimal.ONE)
      .salePrice(BigDecimal.TEN)
			.build();
		
		var products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		
		return products;
		// @formatter:on
	}
}
