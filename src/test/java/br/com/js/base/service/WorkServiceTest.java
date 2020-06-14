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
import br.com.js.base.helper.WorkTestHelper;
import br.com.js.base.repository.WorkRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WorkServiceTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  WorkService service;

  @MockBean
  WorkRepository repository;

  @Test
  @DisplayName("Deve pesquisar todos os serviços")
  public void Should_ReturnList_FindAllWorks() throws Exception {
    var works = WorkTestHelper.getWorkList();

    when(repository.findAll()).thenReturn(works);

    var list = service.findAll();

    assertThat(list).isNotEmpty();
    assertThat(list.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("Deve retornar um serviço pelo id")
  public void Should_ReturnWork_When_FindWorkById() throws Exception {
    var optionalWork = Optional.of(WorkTestHelper.getWork());
    when(repository.findById(anyLong())).thenReturn((optionalWork));

    var savedWork = service.findById(1l);
    assertThat(savedWork).isNotNull();
  }

  @Test
  @DisplayName("Deve retornar nulo quando pesquisar por id inválido")
  public void Should_ReturnNull_When_FindWorkByInvalidId() throws Exception {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());

    var work = service.findById(1l);

    assertThat(work).isNull();
  }

  @Test
  @DisplayName("Deve retornar uma lista de produtos ao pesquisa por parte da descrição")
  public void Should_ReturnList_When_FindWorksByName() throws Exception {
    var works = WorkTestHelper.getWorkList();
    when(repository.findByNameIgnoreCaseContaining(anyString())).thenReturn(works);

    var list = service.findByNameIgnoreCaseContaining("filtro");

    assertThat(list.size()).isEqualTo(2);
  }

  @Test
  @DisplayName("Deve retornar erro ao pesquisas por nome sem o nome")
  public void Should_ThroeException_When_FindWorksByName() throws Exception {

    var exception = Assertions.catchThrowable(() -> service.findByNameIgnoreCaseContaining(null));

    assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("message",
        "Nome precisa ser preenchido.");
  }

  @Test
  @DisplayName("Deve salvar um serviço")
  public void Should_ReturnWork_When_SaveWork() {
    var work = WorkTestHelper.getWork();

    when(repository.save(work)).thenReturn(WorkTestHelper.getWork(1l));

    var savedWork = service.save(work);

    assertThat(savedWork.getId()).isNotNull();
    assertThat(savedWork.getName()).asString().contains("Filtro");
  }

  @Test
  @DisplayName("Deve remover um serviço")
  public void Should_DeleteWork() throws Exception {
    when(repository.existsById(anyLong())).thenReturn(true);

    service.delete(123l);

    verify(repository, atLeastOnce()).deleteById(anyLong());
  }

  @Test
  @DisplayName("Deve retornar exceção ao tentar remover um serviço com id inválido")
  public void Should_ThrowException_When_DeleteInvalidWork() throws Exception {
    doNothing().when(repository).deleteById(anyLong());

    var exception = Assertions.catchThrowable(() -> service.delete(1l));

    assertThat(exception).isInstanceOf(BusinessException.class).hasFieldOrPropertyWithValue("message",
        "Serviço inexistente");
  }

  @Test
  @DisplayName("Deve retornar erro ao criar um serviço com dados incompletos")
  public void Should_ThrowException_When_SaveWorkWithoutName() {
    var work = WorkTestHelper.getWork();
    work.setName(null);

    when(repository.save(work)).thenThrow(DataIntegrityViolationException.class);

    var exception = Assertions.catchThrowable(() -> service.save(work));

    assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("Deve alterar um serviço")
  public void Should_ReturnUpdatedWork_When_UpdateWork() throws Exception {
    var work = WorkTestHelper.getWork(1l);

    when(repository.findById(1l)).thenReturn(Optional.of(work));
    when(repository.save(work)).thenReturn(work);

    var savedWork = service.update(work);

    assertThat(savedWork.getName()).isEqualTo(work.getName());
    verify(repository, Mockito.atLeastOnce()).save(work);
  }

  @Test
  @DisplayName("Deve retornar erro ao alterar um serviço não existente")
  public void Should_ThrowException_When_UpdateInvalidClient() throws Exception {
    var work = WorkTestHelper.getWork();

    when(repository.findById(anyLong())).thenThrow(new ResourceNotFoundException("Cliente não existe"));

    var exception = Assertions.catchThrowable(() -> service.update(work));

    assertThat(exception).isInstanceOf(ResourceNotFoundException.class);
    verify(repository, never()).save(work);
  }
}
