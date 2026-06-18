package com.billsplitter.service;

import com.billsplitter.dto.bill.BillResponseDTO;
import com.billsplitter.model.Item;
import com.billsplitter.model.Order;
import com.billsplitter.model.Participant;
import com.billsplitter.model.User;
import com.billsplitter.repository.ItemParticipantRepository;
import com.billsplitter.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BillServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemParticipantRepository itemParticipantRepository;

    @Mock
    private AccessService accessService;

    @InjectMocks
    private BillService billService;

    @Test
    void shouldGenerateBillSuccessfully() {
        // Arrange
        Long orderId = 1L;

        User currentUser = new User();
        currentUser.setId(1L);

        Order order = new Order();
        order.setId(orderId);
        order.setRestaurantName("Dunkies");
        order.setTax(new BigDecimal(13.00));
        order.setTip(new BigDecimal(13.00));
        order.setParticipants(new ArrayList<>());

        when(accessService.getOrderByIdIfParticipant(orderId, currentUser))
                .thenReturn(order);
        when(itemRepository.findAllByOrder(order))
                .thenReturn(List.of());
        when(itemParticipantRepository.findByParticipant_Order(order))
                .thenReturn(List.of());

        // Act
        BillResponseDTO result = billService.generateBill(orderId, currentUser);

        // Assert
        assertNotNull(result);
        assertEquals("Dunkies", result.getRestaurantName());
        assertEquals(new BigDecimal("0.00"), result.getSubtotal());
    }

    @Test
    void shouldCalculateBillCorrectlyWithItems() {
        // Arrange
        Long orderId = 1L;

        User currentUser = new User();
        currentUser.setId(1L);

        Order order = new Order();
        order.setId(orderId);
        order.setRestaurantName("Dunkies");
        order.setTax(new BigDecimal(13.00));
        order.setTip(new BigDecimal(13.00));
        order.setParticipants(new ArrayList<>());

        Item item = new Item();
        item.setId(1L);
        item.setOrder(order);
        item.setName("Divorced Eggs");
        item.setPrice(new BigDecimal(4.50));
        item.setQuantity(1);

        when(accessService.getOrderByIdIfParticipant(orderId, currentUser))
                .thenReturn(order);
        when(itemRepository.findAllByOrder(order))
                .thenReturn(List.of(item));
        when(itemParticipantRepository.findByParticipant_Order(order))
                .thenReturn(List.of());

        // Act
        BillResponseDTO result = billService.generateBill(orderId, currentUser);

        // Assert
        assertNotNull(result);
        assertEquals("Dunkies", result.getRestaurantName());
        assertEquals(new BigDecimal("4.50"), result.getSubtotal());
        assertEquals(new BigDecimal("0.59"), result.getTaxAmount());
        assertEquals(new BigDecimal("0.59"), result.getTipAmount());
        assertEquals(new BigDecimal("5.67"), result.getTotal());
    }

    @Test
    void shouldReturnZeroForParticipantWithNoItems() {
        // Arrange
        Long orderId = 1L;

        User currentUser = new User();
        currentUser.setId(1L);

        Order order = new Order();
        order.setId(orderId);
        order.setRestaurantName("Dunkies");
        order.setTax(new BigDecimal(13.00));
        order.setTip(new BigDecimal(13.00));

        Item item = new Item();
        item.setId(1L);
        item.setOrder(order);
        item.setName("Divorced Eggs");
        item.setPrice(new BigDecimal(4.50));
        item.setQuantity(1);

        User participantUser = new User();
        participantUser.setId(2L);
        participantUser.setName("Kevin");

        Participant participant = new Participant();
        participant.setId(1L);
        participant.setUser(participantUser);
        participant.setOrder(order);

        order.setParticipants(new ArrayList<>(List.of(participant)));

        when(accessService.getOrderByIdIfParticipant(orderId, currentUser))
                .thenReturn(order);
        when(itemRepository.findAllByOrder(order))
                .thenReturn(List.of(item));
        when(itemParticipantRepository.findByParticipant_Order(order))
                .thenReturn(List.of());

        // Act
        BillResponseDTO result = billService.generateBill(orderId, currentUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getBreakdown().size());
        assertEquals(new BigDecimal("0.00"), result.getBreakdown().get(0).getSubtotal());
        assertEquals(new BigDecimal("0.00"), result.getBreakdown().get(0).getTotal());
    }
}
