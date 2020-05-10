package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;

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

import br.com.js.base.model.OrdemServico;
import br.com.js.base.repository.OrdemServicoRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrdemServicoServiceTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	OrdemServicoService service;

	@MockBean
	OrdemServicoRepository repository;

	@Test
	@DisplayName("Deve salvar uma Ordem de Servico")
	public void deve_salvar_uma_ordem_servico() {
		// Cenário
		var os = novaOrdemServico();

		// @formatter:off
		Mockito.when(repository.save(os))
			.thenReturn(OrdemServico.builder()
				.id(10l)
				.numero(10)
				.ano(2020)
				.build());
		// @formatter:on

		// Execução
		var ordemServicoSalva = service.save(os);

		// Verificação
		assertThat(ordemServicoSalva.getId()).isNotNull();
		assertThat(ordemServicoSalva.getNumero()).isEqualTo(10);
	}

	private OrdemServico novaOrdemServico() {
		// @formatter:off
		return OrdemServico.builder()
			.numero(1)
			.ano(2020)
			.build();
		// @formatter:on
	}
}
