package br.com.js.base.helper;

import java.util.ArrayList;

import br.com.js.base.dto.ClienteDTO;
import br.com.js.base.model.Cliente;

public final class ClienteTestHelper {

	public static ClienteDTO getClienteDTO() {
		return getClienteDTO(null);
	}
	
	public static ClienteDTO getClienteDTO(Long id) {
		// @formatter:off
		var dto = ClienteDTO.builder()
			.id(id)
			.nome("Jayme Sanches")
			.cpf("12345678909")
			.email("jayme@email.com")
			.telefone("55554433")
			.build();
		return dto;
		// @formatter:on
	}
	
	public static Cliente getCliente() {
		return getCliente(null);
	}
	
	public static Cliente getCliente(Long id) {
		// @formatter:off
		var cliente = Cliente.builder()
			.id(id)
			.nome("Jayme Sanches")
			.cpf("12345678909")
			.email("jayme@email.com")
			.telefone("55554433")
			.build();
		return cliente;
		// @formatter:on
	}
	
	public static ArrayList<Cliente> obterListaComDoisClientes() {
		// @formatter:off
		var cliente1 = Cliente.builder()
			.nome("Jayme")
			.cpf("12345678909")
			.email("jayme@email.com")
			.build();

		var cliente2 = Cliente.builder()
			.nome("Isabela")
			.cpf("12345678909")
			.email("isa@email.com")
			.build();

		var clientes = new ArrayList<Cliente>();
		clientes.add(cliente1);
		clientes.add(cliente2);
		
		return clientes;
		// @formatter:on
	}

}
