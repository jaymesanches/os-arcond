package br.com.js.base.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.js.base.dto.UserDTO;
import br.com.js.base.event.CreatedResourceEvent;
import br.com.js.base.model.User;
import br.com.js.base.service.UserService;

@RestController
@RequestMapping("/users")
public class UserResource {

  @Autowired
  private UserService service;

  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  private ModelMapper modelMapper;

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
    var user = service.findById(id);

    if (user == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(toDTO(user));
    }
  }

  @GetMapping
  public ResponseEntity<List<UserDTO>> findByName(@RequestParam(required = false, defaultValue = "%") String name) {
    var users = service.findByNameIgnoreCaseContaining(name);
    return ResponseEntity.ok(toListDTO(users));
  }

  @GetMapping("filter")
  public Page<UserDTO> find(UserDTO dto, Pageable pageRequest) {
    var user = toEntity(dto);
    var result = service.find(user, pageRequest);
    var list = toListDTO(result.getContent());
    
    return new PageImpl<UserDTO>(list, pageRequest, result.getTotalElements());
  }

  @PostMapping
  public ResponseEntity<UserDTO> save(@RequestBody @Valid UserDTO userDTO, HttpServletResponse response) {
    var user = toEntity(userDTO);
    User savedUser = service.save(user);
    publisher.publishEvent(new CreatedResourceEvent(this, response, savedUser.getId()));
    var dto = toDTO(savedUser);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @PutMapping()
  public ResponseEntity<UserDTO> update(@Valid @RequestBody UserDTO userDTO, HttpServletResponse response) {
    var user = toEntity(userDTO);
    var updatedUser = service.update(user);
    var dto = toDTO(updatedUser);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    if (service.findById(id) == null) {
      throw new ResourceNotFoundException();
    }

    service.delete(id);
  }

  private User toEntity(UserDTO userDTO) {
    return modelMapper.map(userDTO, User.class);
  }

  private UserDTO toDTO(User user) {
    return modelMapper.map(user, UserDTO.class);
  }

  private List<UserDTO> toListDTO(List<User> users) {
    return users.stream().map(user -> toDTO(user)).collect(Collectors.toList());
  }
}
