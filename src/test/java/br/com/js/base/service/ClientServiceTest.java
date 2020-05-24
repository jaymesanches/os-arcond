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
import br.com.js.base.helper.ClientTestHelper;
import br.com.js.base.model.Client;
import br.com.js.base.repository.ClientRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClientServiceTest {

	@Autowired
	MockMvc mvc;

	@Autowired
	ClientService service;

	@MockBean
	ClientRepository repository;

	@Test
	@DisplayName("Deve pesquisar todos os clientes")
	public void Should_ReturnList_FindAllClients() throws Exception {
		var clients = ClientTestHelper.getClientList();
		when(repository.findAll()).thenReturn(clients);

		var list = service.findAll();

		assertThat(list).isNotEmpty();
		assertThat(list.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Deve retornar um cliente pelo id")
	public void Should_ReturnClient_When_FindClientById() throws Exception {
		var optional = Optional.of(ClientTestHelper.getClient());
		when(repository.findById(anyLong())).thenReturn((optional));

		var client = service.findById(1l);
		
		assertThat(client).isNotNull();
	}

	@Test
	@DisplayName("Deve retornar nulo quando pesquisar por id inválido")
	public void Should_ReturnNull_When_FindClientByInvalidId() throws Exception {
		when(repository.findById(anyLong())).thenReturn(Optional.empty());

		var client = service.findById(1l);
		
		assertThat(client).isNull();
	}

	@Test
	@DisplayName("Deve retornar uma lista de clientes ao pesquisa por parte do nome")
	public void Should_ReturnList_When_FindClientsByName() throws Exception {
		var clients = ClientTestHelper.getClientList();
		when(repository.findByNameIgnoreCaseContaining(anyString())).thenReturn(clients);

		var list = service.findByNameIgnoreCaseContaining("ze");
		
		assertThat(list.size()).isEqualTo(2);
	}

	@Test
	@DisplayName("Deve salvar um cliente")
	public void Should_ReturnClient_When_SaveClient() {
		var client = ClientTestHelper.getClient(1l);

		when(repository.save(client)).thenReturn(client);

		var savedClient = service.save(client);

		assertThat(savedClient.getId()).isNotNull();
		assertThat(savedClient.getName()).isEqualTo(ClientTestHelper.NAME);
	}

	@Test
	@DisplayName("Deve remover um cliente")
	public void Should_DeleteClient() throws Exception {
		when(repository.existsById(anyLong())).thenReturn(true);
		
		service.delete(123l);
		
		verify(repository, atLeastOnce()).deleteById(anyLong());
	}

	@Test
	@DisplayName("Deve retornar exceção ao tentar remover um cliente com id inválido")
	public void Should_ThrowException_When_DeleteInvalidClient() throws Exception {
		doNothing().when(repository).deleteById(anyLong());

		Throwable exception = Assertions.catchThrowable(() -> service.delete(1l));

		assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("message",
				"Cliente inexistente");
	}

	@Test
	@DisplayName("Deve retornar erro ao criar um cliente com dados incompletos")
	public void Should_ThrowException_When_SaveClientWithoutName() {
		var client = ClientTestHelper.getClient();
		client.setName(null);

		when(repository.save(client)).thenThrow(DataIntegrityViolationException.class);

		Throwable exception = Assertions.catchThrowable(() -> service.save(client));

		assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
	}
	
	@Test
	@DisplayName("Deve alterar um cliente")
	public void Should_ReturnUpdatedClient_When_UpdateClient() throws Exception {
		var client = ClientTestHelper.getClient(1l);
		var optional = Optional.of(client);
		
		when(repository.findById(1l)).thenReturn(optional);
		when(repository.save(Mockito.any(Client.class))).thenReturn(client);
		
		var updatedClient = service.update(client);
		
		assertThat(updatedClient.getName().equals(client.getName()));
		verify(repository, Mockito.atLeastOnce()).save(client);
	}
	
	@Test
	@DisplayName("Deve retornar erro ao alterar um cliente não existente")
	public void Should_ThrowException_When_UpdateInvalidClient() throws Exception {
		var client = ClientTestHelper.getClient();
		when(repository.findById(anyLong())).thenThrow(new ResourceNotFoundException("Cliente não existe"));
		
		Throwable exception = Assertions.catchThrowable(() -> service.update(client));
		
		assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
		verify(repository, never()).save(client);
	}
}
