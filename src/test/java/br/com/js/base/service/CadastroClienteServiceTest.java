package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.model.Cliente;
import br.com.js.base.repository.ClienteRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CadastroClienteServiceTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	CadastroClienteService service;

	@MockBean
	ClienteRepository repository;

	@Test
	@DisplayName("Deve salvar um cliente")
	public void deve_salvar_um_cliente() {
		// Cenário
		var cliente = novoCliente();

		// @formatter:off
		Mockito.when(repository.save(cliente))
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

//	@Test
//	@DisplayName("Deve retornar erro ao criar um cliente com dados incompletos")
//	public void deve_retornar_erro_ao_criar_cliente_com_dados_incompletos() {
//		// Cenário
//		var cliente = Cliente.builder().cpf("12345678909")
//				.dataNascimento(LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy")))
//				.email("jayme@email.com").build();
//
//		Mockito.when(repository.save(Mockito.any(Cliente.class)))
//				.thenThrow(new BusinessException("Dados insuficientes"));
//
//		// Execução
//		Throwable exception = Assertions.catchThrowable(() -> service.save(cliente));
//
//		// Verificação
//		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("mensagem",
//				"Dados insuficientes");
//		Mockito.verify(repository, Mockito.never()).save(cliente);
//	}

	@Test
	@DisplayName("Deve retornar erro ao pesquisar um cliente por nome sem passar o nome")
	public void deve_retornar_erro_ao_pesquisar_cliente_pelo_nome_sem_nome() {
		// Cenário
		String nome = null;

		// Execução
		Throwable exception = Assertions.catchThrowable(() -> service.findByNome(nome));

		// Verificação
		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("mensagem",
				"Nome precisa ser preenchido");
		Mockito.verify(repository, Mockito.never()).findByNomeIgnoringCaseContaining(nome);
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
