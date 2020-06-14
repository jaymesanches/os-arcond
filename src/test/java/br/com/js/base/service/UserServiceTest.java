package br.com.js.base.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import br.com.js.base.exception.BusinessException;
import br.com.js.base.helper.UserTestHelper;
import br.com.js.base.model.User;
import br.com.js.base.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  UserService service;

  @MockBean
  UserRepository repository;

  @Test
  @DisplayName("Deve pesquisar todos os usuários")
  public void Should_ReturnList_FindAllUsers() throws Exception {
    var users = UserTestHelper.getUserList();

    when(repository.findAll()).thenReturn(users);

    var list = service.findAll();

    assertThat(list).isNotEmpty();
    assertThat(list.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("Deve retornar um usuário pelo id")
  public void Shoul_ReturnUser_When_FindUserById() throws Exception {
    var optional = Optional.of(UserTestHelper.getUser());
    when(repository.findById(anyLong())).thenReturn((optional));

    var user = service.findById(1l);

    assertThat(user).isNotNull();
  }

  @Test
  @DisplayName("Deve retornar nulo quando pesquisar por id inválido")
  public void Should_ReturnNull_When_FindUserByInvalidId() throws Exception {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    var user = service.findById(1l);

    assertThat(user).isNull();
  }

  @Test
  @DisplayName("Deve retornar uma lista de usuários ao pesquisa por parte do nome")
  public void Should_ReturnList_When_FindUsersByName() throws Exception {
    var users = UserTestHelper.getUserList();
    when(repository.findByNameIgnoreCaseContaining(anyString())).thenReturn(users);

    var list = service.findByNameIgnoreCaseContaining("jayme");

    assertThat(list.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("Deve retornar erro ao pesquisar por nome sem o nome")
  public void Should_ThroeException_When_FindWorksByName() throws Exception {

    var exception = Assertions.catchThrowable(() -> service.findByNameIgnoreCaseContaining(null));

    assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("message",
        "Nome precisa ser preenchido.");
  }

  @Test
  @DisplayName("Deve salvar um usuário")
  public void Should_ReturnUser_When_SaveUser() {
    var user = UserTestHelper.getUser();

    when(repository.save(user)).thenReturn(UserTestHelper.getUser(1l));

    var savedUser = service.save(user);

    assertThat(savedUser.getId()).isNotNull();
    assertThat(savedUser.getName()).isEqualTo(user.getName());
  }

  @Test
  @DisplayName("Deve remover um usuário")
  public void Should_DeleteUser() throws Exception {
    when(repository.existsById(anyLong())).thenReturn(true);

    service.delete(123l);

    verify(repository, atLeastOnce()).deleteById(anyLong());
  }

  @Test
  @DisplayName("Deve retornar exceção ao tentar remover um usuário com id inválido")
  public void Should_ThrowException_When_DeleteInvalidUser() throws Exception {
    doNothing().when(repository).deleteById(anyLong());

    Throwable exception = Assertions.catchThrowable(() -> service.delete(1l));

    assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("message",
        "Usuário inexistente");
  }

  @Test
  @DisplayName("Deve retornar erro ao criar um usuário com dados incompletos")
  public void Should_ThrowException_When_SaveUserWithoutName() {
    var user = UserTestHelper.getUser();
    user.setName(null);

    when(repository.save(user)).thenThrow(DataIntegrityViolationException.class);

    Throwable exception = Assertions.catchThrowable(() -> service.save(user));

    assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("Deve alterar um usuário")
  public void Should_ReturnUpdatedUser_When_UpdateUser() throws Exception {
    String name = "Nome alterado";

    var user = UserTestHelper.getUser(1l);
    user.setName(name);

    when(repository.findById(1l)).thenReturn(Optional.of(user));
    when(repository.save(user)).thenReturn(user);

    var updatedUser = service.update(user);

    assertThat(updatedUser.getName()).isEqualTo(name);
    verify(repository, Mockito.atLeastOnce()).save(user);
  }

  @Test
  @DisplayName("Deve retornar erro ao alterar um usuário não existente")
  public void Should_ThrowException_When_UpdateInvalidUser() throws Exception {
    var user = UserTestHelper.getUser();

    when(repository.findById(anyLong())).thenThrow(new ResourceNotFoundException("Usuário não existe"));

    Throwable exception = Assertions.catchThrowable(() -> service.update(user));

    assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    verify(repository, never()).save(user);
  }

//  @SuppressWarnings("unchecked")
  @Test
  @DisplayName("Deve filtrar usuários com paginação")
  public void Should_ReturnUsersPage_When_FindByFilter() throws Exception {
    var user = UserTestHelper.getUser(1l);
    var pageRequest = PageRequest.of(0, 10);
    var list = Arrays.asList(user);
    var page = new PageImpl<User>(list, pageRequest, 1);

    var userExample = ArgumentMatchers.<Example<User>>any();
    var pageRequestClass = Mockito.any(PageRequest.class);

    when(repository.findAll(userExample, pageRequestClass)).thenReturn(page);

    Page<User> result = service.find(user, pageRequest);

    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent()).isEqualTo(list);
    assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
    assertThat(result.getPageable().getPageSize()).isEqualTo(10);
  }
}
