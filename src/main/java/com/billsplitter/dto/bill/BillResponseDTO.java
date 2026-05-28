package com.billsplitter.dto.bill;

import com.billsplitter.dto.participant.ParticipantBillDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BillResponseDTO {
    private Long orderId;
    private String restaurantName;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal tipAmount;
    private BigDecimal total;
    private List<ParticipantBillDTO> breakdown;
}
