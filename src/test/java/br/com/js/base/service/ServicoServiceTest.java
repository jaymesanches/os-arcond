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
import br.com.js.base.helper.ServicoTestHelper;
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
	@DisplayName("Deve pesquisar todos os serviços")
	public void deve_pesquisar_todos_os_servicos() throws Exception {
		// Cenário

		var servicos = ServicoTestHelper.obterListaComDoisServicos();

		when(repository.findAll()).thenReturn(servicos);

		// Execução
		var lista = service.findAll();

		// Verificação
		assertThat(lista).isNotEmpty();
		assertThat(lista.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Deve retornar um serviço pelo id")
	public void deve_retornar_um_servico_pelo_id() throws Exception {
		var servicoOptional = Optional.of(ServicoTestHelper.getServico());
		when(repository.findById(anyLong())).thenReturn((servicoOptional));

		var servico = service.findById(1l);
		assertThat(servico).isNotNull();
	}

	@Test
	@DisplayName("Deve retornar nulo quando pesquisar por id inválido")
	public void deve_retornar_erro_quando_pesquisado_por_id_inválido() throws Exception {
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		var servico = service.findById(1l);
		assertThat(servico).isNull();
	}

	@Test
	@DisplayName("Deve retornar uma lista de produtos ao pesquisa por parte da descrição")
	public void deve_pesquisar_uma_lista_de_produtos_ao_pesquisar_por_parte_da_descricao() throws Exception {
		var servicos = ServicoTestHelper.obterListaComDoisServicos();
		when(repository.findByDescricaoIgnoreCaseContaining(anyString())).thenReturn(servicos);

		var lista = service.findByDescricaoIgnoreCaseContaining("filtro");
		assertThat(lista.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Deve salvar um serviço")
	public void deve_salvar_um_servico() {
		// @formatter:off

		// Cenário
		var servico = ServicoTestHelper.getServico();

		when(repository.save(servico))
			.thenReturn(ServicoTestHelper.getServico(1l));

		// Execução
		var servicoSalvo = service.save(servico);

		// Verificação
		assertThat(servicoSalvo.getId()).isNotNull();
		assertThat(servicoSalvo.getDescricao()).asString().contains("Filtro");
		
		// @formatter:on
	}

	@Test
	@DisplayName("Deve remover um serviço")
	public void deve_remover_um_servico() throws Exception {
		when(repository.existsById(anyLong())).thenReturn(true);
		service.delete(123l);
		verify(repository, atLeastOnce()).deleteById(anyLong());
	}

	@Test
	@DisplayName("Deve retornar exceção ao tentar remover um serviço com id inválido")
	public void deve_retornar_erro_ao_remover_um_servico_por_id_invalido() throws Exception {
		doNothing().when(repository).deleteById(anyLong());
		Throwable exception = Assertions.catchThrowable(() -> service.delete(1l));

		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("mensagem",
				"Serviço inexistente");
	}

	@Test
	@DisplayName("Deve retornar erro ao criar um serviço com dados incompletos")
	public void deve_retornar_erro_ao_criar_servico_com_dados_incompletos() {
		// Cenário
		var servico = ServicoTestHelper.getServico();
		servico.setDescricao(null);

		when(repository.save(servico)).thenThrow(DataIntegrityViolationException.class);

		// Execução
		Throwable exception = Assertions.catchThrowable(() -> service.save(servico));

		// Verificação
		assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve alterar um serviço")
	public void deve_alterar_um_servico() throws Exception {
		var servico = ServicoTestHelper.getServico();
		servico.setId(1l);
		var optional = Optional.of(servico);
		when(repository.findById(1l)).thenReturn(optional);
		when(repository.save(servico)).thenReturn(servico);
		
		var servicoAlterado = service.update(servico);
		
		assertThat(servicoAlterado.getDescricao().equals(servico.getDescricao()));
		verify(repository, Mockito.atLeastOnce()).save(servico);
	}
	
	@Test
	@DisplayName("Deve retornar erro ao alterar um serviço não existente")
	public void deve_retornar_erro_ao_tentar_alterar_um_servico_nao_existente() throws Exception {
		var servico = ServicoTestHelper.getServico();
		when(repository.findById(anyLong())).thenThrow(new ResourceNotFoundException("Cliente não existe"));
		
		Throwable exception = Assertions.catchThrowable(() -> service.update(servico));
		
		assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
		verify(repository, never()).save(servico);
	}
}
