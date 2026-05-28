package com.billsplitter.dto.participant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ParticipantShareDTO {
    private Long participantId;
    private String userName;
    private BigDecimal share;
}
