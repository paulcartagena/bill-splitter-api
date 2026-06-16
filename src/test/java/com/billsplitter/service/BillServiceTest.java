package com.billsplitter.service;

import com.billsplitter.dto.bill.BillResponseDTO;
import com.billsplitter.model.Order;
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

    }

    @Test
    void shouldReturnZeroForParticipantWithNoItems() {

    }
}
