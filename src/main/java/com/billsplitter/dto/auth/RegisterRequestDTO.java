package com.billsplitter.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequestDTO {
        @NotBlank
        @Email(message = "Invalid format.")
        private String email;

        @NotBlank(message = "Name is required.")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters.")
        private String name;

        @NotBlank(message = "Password is required.")
        @Size(min = 6, max = 18, message = "Password must be between 6 and 100 characters.")
        private String password;
}
