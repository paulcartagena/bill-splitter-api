package com.billsplitter.dto.item;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDTO {
    @NotBlank
    private String name;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", message = "Price cannot be negative.")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at leats 1.")
    private int quantity;
}
