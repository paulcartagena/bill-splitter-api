package com.billsplitter.dto.participant;

import com.billsplitter.model.enums.OrderRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParticipantSummaryDTO {
    private Long participantId;
    private Long userId;
    private String userName;
    private String email;
    private OrderRole role;
}
