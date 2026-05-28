package com.billsplitter.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ItemResponseDTO {
    private Long id;
    private Long orderId;
    private String name;
    private BigDecimal price;
    private int quantity;
}
