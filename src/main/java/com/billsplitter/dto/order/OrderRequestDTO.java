package com.billsplitter.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OrderRequestDTO {
    @NotBlank
    private String name;

    @NotBlank
    private String restaurantName;

    @NotNull(message = "Tax is required.")
    @DecimalMin(value = "0.0", message = "Tax cannot be negative.")
    private BigDecimal tax;

    @NotNull(message = "Tip is required.")
    @DecimalMin(value = "0.0", message = "Tip cannot be negative.")
    private BigDecimal tip;
}
