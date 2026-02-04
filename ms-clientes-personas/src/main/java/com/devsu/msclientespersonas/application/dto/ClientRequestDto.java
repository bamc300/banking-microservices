package com.devsu.msclientespersonas.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDto {
  @NotBlank(message = "Name is required")
  @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
  private String name;

  @NotBlank(message = "Gender is required")
  private String gender;

  @NotNull(message = "Age is required")
  @Positive(message = "Age must be positive")
  @Min(value = 18, message = "Age must be at least 18")
  @Max(value = 120, message = "Age must be less than 120")
  private Integer age;

  @NotBlank(message = "Identification is required")
  @Size(min = 5, max = 20, message = "Identification must be between 5 and 20 characters")
  private String identification;

  @NotBlank(message = "Address is required")
  @Size(max = 200, message = "Address must be less than 200 characters")
  private String address;

  @NotBlank(message = "Phone is required")
  @Size(min = 7, max = 15, message = "Phone must be between 7 and 15 characters")
  private String phone;

  @NotBlank(message = "Password is required")
  @Size(min = 4, message = "Password must be at least 4 characters")
  private String password;
}
