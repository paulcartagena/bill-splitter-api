package com.billsplitter.service;

import com.billsplitter.dto.order.OrderRequestDTO;
import com.billsplitter.dto.order.OrderResponseDTO;
import com.billsplitter.dto.participant.ParticipantSummaryDTO;
import com.billsplitter.model.Order;
import com.billsplitter.model.Participant;
import com.billsplitter.model.User;
import com.billsplitter.model.enums.OrderRole;
import com.billsplitter.model.enums.OrderStatus;
import com.billsplitter.repository.*;
import exception.InvalidOrderStatusException;
import exception.ParticipantAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AccessService accessService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemParticipantRepository itemParticipantRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderSuccessfully() {
        User creator = new User();
        creator.setId(1L);

        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setRestaurantName("Pizza Hut");
        orderRequestDTO.setTax(new BigDecimal("13.00"));
        orderRequestDTO.setTip(new BigDecimal("13.00"));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setCreatedBy(creator);
        savedOrder.setStatus(OrderStatus.OPEN);
        savedOrder.setRestaurantName(orderRequestDTO.getRestaurantName());
        savedOrder.setTax(orderRequestDTO.getTax());
        savedOrder.setTip(orderRequestDTO.getTip());

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        // Act
        OrderResponseDTO result = orderService.createOrder(orderRequestDTO, creator);

        // Assert
        assertNotNull(result);
        assertEquals("Pizza Hut", result.getRestaurantName());
        assertEquals(OrderRole.CREATOR, result.getOrderRole());
    }

    @Test
    void shouldAddParticipantSuccessfully() {
        User creator = new User();
        creator.setId(1L);

        Long orderId = 1L;
        String userEmail = "kevin@gmail.com";

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(creator);
        order.setStatus(OrderStatus.OPEN);

        User newParticipant = new User();
        newParticipant.setId(2L);
        newParticipant.setName("Kevin");
        newParticipant.setEmail(userEmail);

        Participant savedParticipant = new Participant();
        savedParticipant.setId(10L);
        savedParticipant.setUser(newParticipant);
        savedParticipant.setOrder(order);

        when(accessService.getOrderByIdIfCreator(orderId, creator))
                .thenReturn(order);
        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(newParticipant));
        when(participantRepository.existsByOrderAndUser(order, newParticipant))
                .thenReturn(false);
        when(participantRepository.save(any(Participant.class)))
                .thenReturn(savedParticipant);

        // Act
        ParticipantSummaryDTO result = orderService.addParticipant(orderId, userEmail, creator);

        // Assert
        assertNotNull(result);
        assertEquals(savedParticipant.getId(), result.getParticipantId());
    }

    @Test
    void shouldFailWhenAddingParticipantToClosedOrder() {
        User creator = new User();
        creator.setId(1L);

        Long orderId = 1L;
        String userEmail = "kevin@gmail.com";

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(creator);
        order.setStatus(OrderStatus.CLOSED);

        User newParticipant = new User();
        newParticipant.setId(2L);
        newParticipant.setName("Kevin");
        newParticipant.setEmail(userEmail);

        when(accessService.getOrderByIdIfCreator(orderId, creator))
                .thenReturn(order);
        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(newParticipant));

        // Act & Assert
        assertThrows(InvalidOrderStatusException.class, () -> {
            orderService.addParticipant(orderId, userEmail, creator);
        });
    }

    @Test
    void shouldFailWhenParticipantAlreadyExists() {
        User creator = new User();
        creator.setId(1L);

        Long orderId = 1L;
        String userEmail = "kevin@gmail.com";

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(creator);
        order.setStatus(OrderStatus.OPEN);

        User newParticipant = new User();
        newParticipant.setId(2L);
        newParticipant.setName("Kevin");
        newParticipant.setEmail(userEmail);

        when(accessService.getOrderByIdIfCreator(orderId, creator))
                .thenReturn(order);
        when(userRepository.findByEmail(userEmail))
                .thenReturn(Optional.of(newParticipant));
        when(participantRepository.existsByOrderAndUser(order, newParticipant))
                .thenReturn(true);

        // Act & Assert
        assertThrows(ParticipantAlreadyExistsException.class, () -> {
            orderService.addParticipant(orderId, userEmail, creator);
        });
    }

    @Test
    void shouldFailToCloseOrderWhenNotOpen() {

    }

    @Test
    void shouldDeleteOrderSuccessfully() {

    }

    @Test
    void shouldAssignItemSuccessfully() {

    }
}
