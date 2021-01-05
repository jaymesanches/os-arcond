package br.com.js.base.resource;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

public abstract class BaseResourceTest {

	public BaseResourceTest() {
		super();
	}
	
	// Simula as requisições http
	@Autowired
	MockMvc mvc;
	
	@MockBean
	private ModelMapper modelMapper;
	
	public String obtainAccessToken(String username, String password) throws Exception {
	  
	  System.out.println("-------------------------");
	  System.out.println("------GETTING TOKEN------");
	  System.out.println("-------------------------");

		var params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", "password");
		params.add("client", "angular");
		params.add("username", username);
		params.add("password", password);

		var result = mvc
				.perform(post("/oauth/token").params(params).with(httpBasic("angular", "@ngul@r0"))
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"));

		var resultString = result.andReturn().getResponse().getContentAsString();
		var jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}
}