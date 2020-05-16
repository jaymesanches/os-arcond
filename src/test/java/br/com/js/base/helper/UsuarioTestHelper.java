package br.com.js.base.helper;

import java.util.ArrayList;

import br.com.js.base.dto.UsuarioDTO;
import br.com.js.base.model.Usuario;

public class UsuarioTestHelper {
	public static Usuario getUsuario() {
		return getUsuario(null);
	}

	public static Usuario getUsuario(Long id) {
		// @formatter:off
		return Usuario.builder()
			.id(id)	
			.nome("Jayme")
			.senha("12345678909")
			.email("jayme@email.com")
			.build();
		// @formatter:on
	}

	public static UsuarioDTO getUsuarioDTO() {
		return getUsuarioDTO(null);
	}

	public static UsuarioDTO getUsuarioDTO(Long id) {
		// @formatter:off
		return UsuarioDTO.builder()
				.id(id)	
				.nome("Jayme")
				.senha("12345678909")
				.email("jayme@email.com")
				.build();
		// @formatter:on
	}

	public static ArrayList<Usuario> obterListaComDoisUsuarios() {
		// @formatter:off
		var usuario1 = Usuario.builder()
			.id(1l)	
			.nome("Jayme")
			.senha("12345678909")
			.email("jayme@email.com")
			.build();

		var usuario2 = Usuario.builder()
			.id(2l)	
			.nome("Isabela")
			.senha("12345678910")
			.email("isa@email.com")
			.build();
		
		var usuarios = new ArrayList<Usuario>();
		usuarios.add(usuario1);
		usuarios.add(usuario2);
		
		return usuarios;
		// @formatter:on
	}
}
