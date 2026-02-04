package com.devsu.msclientespersonas.application.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class ClientResponseDto {
  private UUID clientId;
  private String name;
  private String gender;
  private Integer age;
  private String identification;
  private String address;
  private String phone;
  private boolean status;
  private List<AccountDto> accounts;
}
