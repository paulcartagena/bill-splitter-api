package com.billsplitter.dto.participant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParticipantRequestDTO {
    @NotBlank
    @Email(message = "Invalid format.")
    private String email;
}
