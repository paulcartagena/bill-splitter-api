package com.billsplitter.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDTO {
    @NotBlank
    @Email(message = "Invalid format.")
    private String email;

    @NotBlank
    private String password;
}
