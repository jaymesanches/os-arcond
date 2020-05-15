package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Cliente;
import br.com.js.base.repository.ClienteRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClienteServiceTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ClienteService service;

	@MockBean
	ClienteRepository repository;

	@Test
	@DisplayName("Deve pesquisar todos os clientes")
	public void deve_pesquisar_todos_os_clientes() throws Exception {
		// Cenário

		var clientes = obterListaComDoisClientes();

		when(repository.findAll()).thenReturn(clientes);

		// Execução
		var lista = service.findAll();

		// Verificação
		assertThat(lista).isNotEmpty();
		assertThat(lista.size()).isEqualTo(2);
	}

	private ArrayList<Cliente> obterListaComDoisClientes() {
		// @formatter:off
		var cliente1 = Cliente.builder()
			.nome("Jayme")
			.cpf("12345678909")
			.email("jayme@email.com")
			.build();
		// @formatter:on

		var cliente2 = Cliente.builder().nome("Isabela").cpf("12345678909").email("isa@email.com").build();
		// @formatter:on

		var clientes = new ArrayList<Cliente>();
		clientes.add(cliente1);
		clientes.add(cliente2);
		return clientes;
	}

	@Test
	@DisplayName("Deve retornar um cliente pelo id")
	public void deve_retornar_um_cliente_pelo_id() throws Exception {
		var clienteOptional = Optional.of(novoCliente());
		when(repository.findById(anyLong())).thenReturn((clienteOptional));

		var cliente = service.findById(1l);
		assertThat(cliente).isNotNull();
	}

	@Test
	@DisplayName("Deve retornar nulo quando pesquisar por id inválido")
	public void deve_retornar_erro_quando_pesquisado_por_id_inválido() throws Exception {
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		var cliente = service.findById(1l);
		assertThat(cliente).isNull();
	}

	@Test
	@DisplayName("Deve retornar uma lista de clientes ao pesquisa por parte do nome")
	public void deve_pesquisar_uma_lista_de_clientes_ao_pesquisar_por_parte_do_nome() throws Exception {
		var clientes = obterListaComDoisClientes();
		when(repository.findByNomeIgnoringCaseContaining(anyString())).thenReturn(clientes);

		var lista = service.findByNomeIgnoringCaseContaining("ze");
		assertThat(lista.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Deve salvar um cliente")
	public void deve_salvar_um_cliente() {
		// Cenário
		var cliente = novoCliente();

		// @formatter:off
		when(repository.save(cliente))
			.thenReturn(Cliente.builder()
				.id(10l)
				.nome("Jayme")
				.cpf("12345678909")
				.email("jayme@email.com")
				.build());
		// @formatter:on

		// Execução
		var clienteSalvo = service.save(cliente);

		// Verificação
		assertThat(clienteSalvo.getId()).isNotNull();
		assertThat(clienteSalvo.getNome()).isEqualTo("Jayme");
	}

	@Test
	@DisplayName("Deve remover um cliente")
	public void deve_remover_um_cliente() throws Exception {
		when(repository.existsById(anyLong())).thenReturn(true);
		service.delete(123l);
		verify(repository, atLeastOnce()).deleteById(anyLong());
	}

	@Test
	@DisplayName("Deve retornar exceção ao tentar remover um cliente com id inválido")
	public void deve_retornar_erro_ao_remover_um_cliente_por_id_invalido() throws Exception {
		doNothing().when(repository).deleteById(anyLong());

		Throwable exception = Assertions.catchThrowable(() -> service.delete(1l));

		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("mensagem",
				"Cliente inexistente");
	}

	@Test
	@DisplayName("Deve retornar erro ao criar um cliente com dados incompletos")
	public void deve_retornar_erro_ao_criar_cliente_com_dados_incompletos() {
		// Cenário
		var cliente = novoCliente();
		cliente.setNome(null);

		when(repository.save(cliente)).thenThrow(DataIntegrityViolationException.class);

		// Execução
		Throwable exception = Assertions.catchThrowable(() -> service.save(cliente));

		// Verificação
		assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve alterar um cliente")
	public void deve_alterar_um_cliente() throws Exception {
		var cliente = novoCliente();
		when(repository.save(Mockito.any(Cliente.class))).thenReturn(cliente);
		
		var clienteAlterado = service.update(cliente);
		
		assertThat(clienteAlterado.getNome().equals(cliente.getNome()));
		verify(repository, Mockito.atLeastOnce()).save(cliente);
	}

	private Cliente novoCliente() {
		// @formatter:off
		return Cliente.builder()
			.nome("Jayme")
			.cpf("12345678909")
//			.dataNascimento(LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy")))
			.email("jayme@email.com")
			.build();
		// @formatter:on
	}
}
