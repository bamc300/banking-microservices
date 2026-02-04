package com.devsu.msclientespersonas.application.mapper;

import com.devsu.msclientespersonas.application.dto.ClientRequestDto;
import com.devsu.msclientespersonas.application.dto.ClientResponseDto;
import com.devsu.msclientespersonas.application.dto.AccountDto;
import com.devsu.msclientespersonas.domain.model.Client;
import com.devsu.msclientespersonas.domain.model.Account;
import com.devsu.msclientespersonas.domain.model.Person;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Client toDomain(ClientRequestDto dto) {
        Person person = Person.builder()
                .name(dto.getName())
                .gender(dto.getGender())
                .age(dto.getAge())
                .identification(dto.getIdentification())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .build();

        return Client.builder()
                .clientId(UUID.randomUUID())
                .password(passwordEncoder.encode(dto.getPassword()))
                .status(true)
                .person(person)
                .build();
    }

    public ClientResponseDto toResponseDto(Client client) {
        ClientResponseDto dto = new ClientResponseDto();
        dto.setClientId(client.getClientId());
        dto.setName(client.getPerson().getName());
        dto.setGender(client.getPerson().getGender());
        dto.setAge(client.getPerson().getAge());
        dto.setIdentification(client.getPerson().getIdentification());
        dto.setAddress(client.getPerson().getAddress());
        dto.setPhone(client.getPerson().getPhone());
        dto.setStatus(client.isStatus());
        
        if (client.getAccounts() != null) {
            dto.setAccounts(mapAccounts(client.getAccounts()));
        }
        
        return dto;
    }

    private List<AccountDto> mapAccounts(List<Account> accounts) {
        return accounts.stream()
                .map(c -> AccountDto.builder()
                        .accountId(c.getAccountId())
                        .accountNumber(c.getAccountNumber())
                        .accountType(c.getAccountType())
                        .initialBalance(c.getInitialBalance())
                        .currentBalance(c.getCurrentBalance())
                        .status(c.isStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
