package com.billsplitter.dto.order;

import com.billsplitter.dto.UserSummaryDTO;
import com.billsplitter.dto.participant.ParticipantSummaryDTO;
import com.billsplitter.model.enums.OrderRole;
import com.billsplitter.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private UserSummaryDTO createdBy;
    private String name;
    private String restaurantName;
    private BigDecimal tax;
    private BigDecimal tip;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<ParticipantSummaryDTO> participants;
    private OrderRole orderRole;
}
