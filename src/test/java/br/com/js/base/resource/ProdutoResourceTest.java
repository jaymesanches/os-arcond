package br.com.js.base.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.js.base.dto.ProdutoDTO;
import br.com.js.base.helper.ProdutoTestHelper;
import br.com.js.base.model.Produto;
import br.com.js.base.service.ProdutoService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProdutoResourceTest extends BaseResourceTest {

	private final String URL_API = "/produtos";

	// Simula as requisições http
	@Autowired
	MockMvc mvc;

	@MockBean
	ProdutoService service;

	private String accessToken;

	@BeforeEach
	public void setup() throws Exception {
		accessToken = obtainAccessToken("admin@admin.com", "senhas");
	}

	@Test
	@DisplayName("Deve listar todos os produtos")
	public void deve_listar_todos_os_produtos() throws Exception {
		// @formatter:off
		var request = 
			MockMvcRequestBuilders
			.get(URL_API)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			;

		mvc
			.perform(request)
			.andExpect(status().isOk());
		// @formatter:on
	}

	@Test
	@DisplayName("Deve retornar erro ao criar um produto sem descrição")
	public void deve_retornar_erro_ao_criar_produto_sem_descricao() throws Exception {
		// @formatter:off
		var dto = ProdutoTestHelper.getProdutoDTO();
		dto.setDescricao(null);

		var json = toJson(dto);

		var request = 
			MockMvcRequestBuilders
			.post(URL_API)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);

		var result = mvc
			.perform(request)
			.andExpect(status().isBadRequest())			
			.andReturn().getResolvedException()
			;
		
		assertThat(result).isInstanceOf(MethodArgumentNotValidException.class);
		
		// @formatter:on
	}

	@Test
	@DisplayName("Deve criar um novo produto")
	public void deve_criar_um_novo_produto() throws Exception {
		var produto = ProdutoTestHelper.getProduto(1l);
		var dto = ProdutoTestHelper.getProdutoDTO();

		given(service.save(any(Produto.class))).willReturn(produto);

		var json = toJson(dto);

		// @formatter:off
		var request = 
			MockMvcRequestBuilders
			.post(URL_API)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);

		mvc
			.perform(request)
			.andExpect(status().isCreated())
			.andExpect(jsonPath("id").isNotEmpty())
			.andExpect(jsonPath("descricao").value(dto.getDescricao()));

		// @formatter:on
	}

	@Test
	@DisplayName("Deve deletar um produto existente")
	public void deve_deletar_um_produto() throws Exception {
		// Cenário
		var id = 123l;
		given(service.findById(anyLong())).willReturn(Produto.builder().id(id).build());

		// Execução

		// @formatter:off
		var request = 
			MockMvcRequestBuilders
			.delete(URL_API + "/{id}", "1")
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);

		mvc
			.perform(request)
			.andExpect(status().isNoContent());

		// @formatter:on
	}

	@Test
	@DisplayName("Deve retornar not found ao deletar um produto inexistente")
	public void deve_retornar_not_found_ao_deletar_um_produto_inexistente() throws Exception {
		// Cenário
		doThrow(new ResourceNotFoundException()).when(service).delete(anyLong());

		// Execução
		var request = MockMvcRequestBuilders.delete(URL_API + "/{id}", 1l).header("Authorization",
				"Bearer " + accessToken);

		mvc.perform(request).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Deve retornar uma lista de produtos por nome")
	public void deve_pesquisar_uma_lista_produto_por_nome() throws Exception {
		// @formatter:off

		var produto = ProdutoTestHelper.getProduto();
		var lista = new ArrayList<Produto>();
		lista.add(produto);

		// Cenário
		given(service.findByDescricaoIgnoreCaseContaining(anyString())).willReturn(lista);

		// Execução
		var request = MockMvcRequestBuilders
				.get(URL_API + "?descricao=teste")
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);

		mvc.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$..descricao").exists());
		// @formatter:on
	}

	@Test
	@DisplayName("Deve alterar um produto")
	public void deve_alterar_um_produto() throws Exception {
		// Cenário
		var produto = ProdutoTestHelper.getProduto(1l);
		produto.setDescricao("Descrição Alterada");
		given(service.update(any(Produto.class))).willReturn(produto);

		var dto = ProdutoTestHelper.getProdutoDTO(1l);

		var json = toJson(dto);

		// Execução

		// @formatter:off
		var request = 
			MockMvcRequestBuilders
			.put(URL_API)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(json);

		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("id").value(dto.getId()))
			.andExpect(jsonPath("descricao").value("Descrição Alterada"));

		// @formatter:on
	}

	@Test
	@DisplayName("Deve buscar um produto pelo código")
	public void deve_buscar_um_produto_pelo_codigo() throws Exception {
		// Cenário
		var produto = ProdutoTestHelper.getProduto(1l);
		given(service.findById(anyLong())).willReturn(produto);

		// Execução
		// @formatter:off
		var request = 
			MockMvcRequestBuilders
			.get(URL_API + "/{id}", 1l)
			.header("Authorization", "Bearer " + accessToken)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON);

		mvc
			.perform(request)
			.andExpect(status().isOk())
			.andExpect(jsonPath("descricao").value(produto.getDescricao()));

        // @formatter:on
	}

	private String toJson(ProdutoDTO dto) throws JsonProcessingException {
		var objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		var json = objectMapper.writeValueAsString(dto);
		return json;
	}
}
