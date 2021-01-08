package br.com.js.base.helper;

import br.com.js.base.dto.PhoneDTO;
import br.com.js.base.model.Phone;

public class PhoneTestHelper {

  public static Phone getPhone() {
    // @formatter:off
    var phone = Phone.builder()
      .number("5555555")
      .build();
    
    return phone;
  }
  
  public static PhoneDTO getPhoneDTO() {
    // @formatter:off
    var dto = PhoneDTO.builder()
      .number("5555555")
      .build();
    
    return dto;
    // @formatter:on
  }
}
