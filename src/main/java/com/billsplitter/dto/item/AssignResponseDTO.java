package com.billsplitter.dto.item;

import com.billsplitter.dto.participant.ParticipantShareDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AssignResponseDTO {
    private Long itemId;
    private String itemName;
    private BigDecimal totalPrice;
    private List<ParticipantShareDTO> shares;
}
