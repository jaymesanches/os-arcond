package br.com.js.base.resource;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import br.com.js.base.BaseAuthApplication;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest(classes = BaseAuthApplication.class)
public class OAuthMvcTest {
  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
  }

  private String obtainAccessToken(String username, String password) throws Exception {

    var params = new LinkedMultiValueMap<String, String>();
    params.add("grant_type", "password");
    params.add("client", "angular");
    params.add("username", username);
    params.add("password", password);

    var result = mockMvc
        .perform(post("/oauth/token").params(params).with(httpBasic("angular", "@ngul@r0"))
            .accept("application/json;charset=UTF-8"));
        
//    .andExpect(status().isOk())
//    .andExpect(content().contentType("application/json;charset=UTF-8"));

    if(result == null) {
      throw new BadCredentialsException("Invalid User or Password");
    }
    
    var resultString = result.andReturn().getResponse().getContentAsString();
    var jsonParser = new JacksonJsonParser();
    return jsonParser.parseMap(resultString).get("access_token").toString();
  }

  @Test
  public void givenNoToken_whenGetSecureRequest_thenUnauthorized() throws Exception {
    mockMvc.perform(get("/clients")).andExpect(status().isUnauthorized());
  }

  @Test
  public void givenInvalidRole_whenGetSecureRequest_thenForbidden() throws Exception {
    //String accessToken = obtainAccessToken("user1", "pass");
    
    mockMvc.perform(get("/clients").header("Authorization", "Bearer " + "invalidToken")).andExpect(status().isUnauthorized());
  }
}
