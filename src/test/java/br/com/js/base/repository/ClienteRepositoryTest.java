package br.com.js.base.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.js.base.model.Cliente;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ClienteRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	ClienteRepository repository;
	
	@Test
	@DisplayName("Deve retornar true se existir cliente com o nome informado")
	public void deve_retornar_true_se_existir_cliente_com_o_nome_informado() {
		//Cenário
		var cliente = novoCliente();
		entityManager.persist(cliente);
		String nome = "Jayme";
		
		//Execução
		boolean exists = repository.existsByNomeIgnoringCaseContaining(nome);
		
		//Verificação
		assertThat(exists).isTrue();
	}
	
	
	
	
	private Cliente novoCliente() {
		// @formatter:off
		return Cliente.builder()
			.nome("Jayme")
			.cpf("12345678909")
//			.dataNascimento(LocalDate.parse("01/01/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy")))
			.email("jayme@email.com")
			.telefone("5555-5555")
			.build();
		// @formatter:on
	}
}
