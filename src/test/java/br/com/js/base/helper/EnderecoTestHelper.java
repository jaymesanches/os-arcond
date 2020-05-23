package br.com.js.base.helper;

import java.util.Arrays;
import java.util.List;

import br.com.js.base.dto.EnderecoDTO;
import br.com.js.base.model.Endereco;

public class EnderecoTestHelper {

  private static final String UNIDADE = "10";
  private static final String UF = "PR";
  private static final String CIDADE = "Curitiba";
  private static final String BAIRRO = "Centro";
  private static final String COMPLEMENTO = "Apto 101";
  private static final String LOGRADOURO = "Rua das Palmeiras";
  private static final String CEP = "80000000";

  public static Endereco getEndereco() {
    return getEndereco(null);
  }

  public static Endereco getEndereco(Long id) {
    // @formatter:off
    var endereco = Endereco.builder()
      .id(id)
      .cep(CEP)
      .logradouro(LOGRADOURO)
      .complemento(COMPLEMENTO)
      .bairro(BAIRRO)
      .localidade(CIDADE)
      .uf(UF)
      .unidade(UNIDADE)
      .ibge("123")
      .gia("321")
      .build();
    
    return endereco;
    // @formatter:on
  }

  public static EnderecoDTO getEnderecoDTO() {
    return getEnderecoDTO(null);
  }

  public static EnderecoDTO getEnderecoDTO(Long id) {
    // @formatter:off
    var dto = EnderecoDTO.builder()
        .id(id)
        .cep(CEP)
        .logradouro(LOGRADOURO)
        .localidade(CIDADE)
        .complemento(COMPLEMENTO)
        .bairro(BAIRRO)
        .uf(UF)
        .unidade(UNIDADE)
        .build();
    
    return dto;
    // @formatter:on
  }

  public static List<Endereco> getAddressList() {
    return Arrays.asList(getEndereco(1l), getEndereco(2l));
  }
  
  public static List<EnderecoDTO> getAddressDTOList() {
    return Arrays.asList(getEnderecoDTO(1l), getEnderecoDTO(2l));
  }
}
