package com.billsplitter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserSummaryDTO {
    private Long id;
    private String name;
    private String email;
}
