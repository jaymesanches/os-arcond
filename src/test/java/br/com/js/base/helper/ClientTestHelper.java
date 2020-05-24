package br.com.js.base.helper;

import java.util.ArrayList;

import br.com.js.base.dto.ClientDTO;
import br.com.js.base.model.Client;

public final class ClientTestHelper {

	public static final String NAME = "Jayme Sanches";

  public static ClientDTO getClientDTO() {
		return getClientDTO(null);
	}
	
	public static ClientDTO getClientDTO(Long id) {
		// @formatter:off
		var dto = ClientDTO.builder()
			.id(id)
			.name(NAME)
			.document("12345678909")
			.email("jayme@email.com")
			.phone("55554433")
			.build();
		
		return dto;
		// @formatter:on
	}
	
	public static Client getClient() {
		return getClient(null);
	}
	
	public static Client getClient(Long id) {
		// @formatter:off
		var client = Client.builder()
			.id(id)
			.name(NAME)
			.document("12345678909")
			.email("jayme@email.com")
			.phone("55554433")
			.build();
		
		return client;
		// @formatter:on
	}
	
	public static ArrayList<Client> getClientList() {
		// @formatter:off
		var client1 = Client.builder()
			.name("Jayme")
			.document("12345678909")
			.email("jayme@email.com")
			.build();

		var client2 = Client.builder()
			.name("Isabela")
			.document("12345678909")
			.email("isa@email.com")
			.build();

		var clients = new ArrayList<Client>();
		clients.add(client1);
		clients.add(client2);
		
		return clients;
		// @formatter:on
	}

}
