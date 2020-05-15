package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

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

import br.com.js.base.model.Servico;
import br.com.js.base.repository.ServicoRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ServicoServiceTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ServicoService service;

	@MockBean
	ServicoRepository repository;

	@Test
	@DisplayName("Deve salvar um serviço")
	public void deve_salvar_um_servico() {
		// Cenário
		var servico = novoServico();
		Mockito.when(repository.save(servico))
				.thenReturn(Servico.builder().id(10l).codigo("1").descricao("teste").preco(BigDecimal.TEN).build());

		// Execução
		var servicoSalvo = service.save(servico);

		// Verificação
		assertThat(servicoSalvo.getId()).isNotNull();
		assertThat(servicoSalvo.getDescricao()).isEqualTo("teste");
	}
	
	private Servico novoServico() {
		return Servico.builder().codigo("1").descricao("teste").preco(BigDecimal.TEN).build();
	}
}
