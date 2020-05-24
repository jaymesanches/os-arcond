package br.com.js.base.helper;

import java.util.Arrays;
import java.util.List;

import br.com.js.base.dto.AddressDTO;
import br.com.js.base.model.Address;

public class AddressTestHelper {

  private static final String STATE = "PR";
  private static final String CITY = "Curitiba";
  private static final String NEIGHBORHOOD = "Centro";
  private static final String STREET = "Rua das Palmeiras";
  private static final String CEP = "80000000";

  public static Address getAddress() {
    return getAddress(null);
  }

  public static Address getAddress(Long id) {
    // @formatter:off
    var address = Address.builder()
      .id(id)
      .cep(CEP)
      .street(STREET)
      .neighborhood(NEIGHBORHOOD)
      .city(CITY)
      .state(STATE)
      .build();
    
    return address;
    // @formatter:on
  }

  public static AddressDTO getAddressDTO() {
    return getAddressDTO(null);
  }

  public static AddressDTO getAddressDTO(Long id) {
    // @formatter:off
    var dto = AddressDTO.builder()
        .id(id)
        .cep(CEP)
        .street(STREET)
        .neighborhood(NEIGHBORHOOD)
        .city(CITY)
        .state(STATE)
        .build();
    
    return dto;
    // @formatter:on
  }

  public static List<Address> getAddressesList() {
    return Arrays.asList(getAddress(1l), getAddress(2l));
  }
  
  public static List<AddressDTO> getAddressDTOList() {
    return Arrays.asList(getAddressDTO(1l), getAddressDTO(2l));
  }
}
