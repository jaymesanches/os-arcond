package br.com.js.base.resource;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.js.base.dto.EnderecoDTO;
import br.com.js.base.event.RecursoCriadoEvent;
import br.com.js.base.model.Endereco;
import br.com.js.base.repository.EnderecoRepository;

@RestController
@RequestMapping("/enderecos")
public class EnderecoResource {

  @Autowired
  private EnderecoRepository enderecoRepository;

  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  private ModelMapper modelMapper;

  @GetMapping
  public List<Endereco> listar() {
    return enderecoRepository.findAll();
  }

  @GetMapping("/{codigo}")
  public ResponseEntity<Endereco> buscarPeloCodigo(@PathVariable Long codigo) {
    Optional<Endereco> endereco = enderecoRepository.findById(codigo);
    return endereco.isPresent() ? ResponseEntity.ok(endereco.get()) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<EnderecoDTO> salvar(@RequestBody EnderecoDTO enderecoDTO, HttpServletResponse response) {
    Endereco enderecoSalvo = enderecoRepository.save(toEntity(enderecoDTO));
    publisher.publishEvent(new RecursoCriadoEvent(this, response, enderecoSalvo.getId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(toEnderecoDTO(enderecoSalvo));
  }

  private EnderecoDTO toEnderecoDTO(Endereco endereco) {
    return modelMapper.map(endereco, EnderecoDTO.class);
  }

  private Endereco toEntity(EnderecoDTO enderecoDTO) {
    return modelMapper.map(enderecoDTO, Endereco.class);
  }
}
