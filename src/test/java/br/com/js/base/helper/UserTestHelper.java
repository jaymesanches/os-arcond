package br.com.js.base.helper;

import java.util.ArrayList;

import br.com.js.base.dto.UserDTO;
import br.com.js.base.model.User;

public class UserTestHelper {
	public static User getUser() {
		return getUser(null);
	}

	public static User getUser(Long id) {
		// @formatter:off
		return User.builder()
			.id(id)	
			.name("Jayme")
			.password("12345678909")
			.email("jayme@email.com")
			.build();
		// @formatter:on
	}

	public static UserDTO getUserDTO() {
		return getUserDTO(null);
	}

	public static UserDTO getUserDTO(Long id) {
		// @formatter:off
		return UserDTO.builder()
	      .id(id) 
	      .name("Jayme")
	      .password("12345678909")
	      .email("jayme@email.com")
	      .build();
		// @formatter:on
	}

	public static ArrayList<User> getUserList() {
		// @formatter:off
		var user1 = User.builder()
			.id(1l)	
			.name("Jayme")
			.password("12345678909")
			.email("jayme@email.com")
			.build();

		var user2 = User.builder()
			.id(2l)	
			.name("Isabela")
			.password("12345678910")
			.email("isa@email.com")
			.build();
		
		var users = new ArrayList<User>();
		users.add(user1);
		users.add(user2);
		
		return users;
		// @formatter:on
	}
}
