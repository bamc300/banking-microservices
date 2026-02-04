package com.devsu.msclientespersonas.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {
  private UUID clientId;
  private String password;
  private boolean status;
  private Person person;
  private List<Account> accounts;
}
