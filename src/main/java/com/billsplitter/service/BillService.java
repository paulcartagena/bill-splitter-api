package com.billsplitter.service;

import com.billsplitter.dto.bill.BillResponseDTO;
import com.billsplitter.dto.participant.ParticipantBillDTO;
import com.billsplitter.model.*;
import com.billsplitter.repository.ItemParticipantRepository;
import com.billsplitter.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillService {

    private final ItemParticipantRepository itemParticipantRepository;
    private final ItemRepository itemRepository;
    private final AccessService accessService;

    public BillService(ItemParticipantRepository itemParticipantRepository,
                       ItemRepository itemRepository,
                       AccessService accessService) {
        this.itemParticipantRepository = itemParticipantRepository;
        this.itemRepository = itemRepository;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public BillResponseDTO generateBill(Long orderId, User currentUser) {
        Order order = accessService.getOrderByIdIfParticipant(orderId, currentUser);
        List<Item> items = itemRepository.findAllByOrder(order);

        List<ItemParticipant> allItemParticipants = itemParticipantRepository.findByParticipant_Order(order);
        Map<Participant, List<ItemParticipant>> byParticipant = allItemParticipants
                .stream()
                .collect(Collectors.groupingBy(ItemParticipant::getParticipant));

        BigDecimal subtotal = items
                .stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxAmount = applyPercentage(subtotal, order.getTax());
        BigDecimal tipAmount = applyPercentage(subtotal, order.getTip());
        BigDecimal total = subtotal.add(taxAmount).add(tipAmount);

        List<ParticipantBillDTO> breakdown = order.getParticipants()
                .stream()
                .map(participant -> {
                    List<ItemParticipant> itemParticipants = byParticipant.getOrDefault(participant, List.of());

                    BigDecimal participantSubtotal = itemParticipants
                            .stream()
                            .map(ItemParticipant::getShare)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Calculate tax, tip and total
                    BigDecimal participantTax = applyPercentage(participantSubtotal, order.getTax());
                    BigDecimal participantTip = applyPercentage(participantSubtotal, order.getTip());
                    BigDecimal participantTotal = participantSubtotal.add(participantTax).add(participantTip);

                    // Build ParticipantBillDTO
                    return new ParticipantBillDTO(
                            participant.getId(),
                            participant.getUser().getName(),
                            participantSubtotal.setScale(2, RoundingMode.HALF_UP),
                            participantTax.setScale(2, RoundingMode.HALF_UP),
                            participantTip.setScale(2, RoundingMode.HALF_UP),
                            participantTotal.setScale(2, RoundingMode.HALF_UP)
                    );
                })
                .toList();

        return new BillResponseDTO(
                order.getId(),
                order.getRestaurantName(),
                subtotal.setScale(2, RoundingMode.HALF_UP),
                taxAmount.setScale(2, RoundingMode.HALF_UP),
                tipAmount.setScale(2, RoundingMode.HALF_UP),
                total.setScale(2, RoundingMode.HALF_UP),
                breakdown
        );
    }

    private BigDecimal applyPercentage(BigDecimal amount, BigDecimal percentage) {
        return amount.multiply(percentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
    }
}
