package com.billsplitter.service;

import com.billsplitter.dto.item.AssignRequestDTO;
import com.billsplitter.dto.item.AssignResponseDTO;
import com.billsplitter.dto.order.OrderRequestDTO;
import com.billsplitter.dto.order.OrderResponseDTO;
import com.billsplitter.dto.participant.ParticipantSummaryDTO;
import com.billsplitter.model.*;
import com.billsplitter.model.enums.OrderRole;
import com.billsplitter.model.enums.OrderStatus;
import com.billsplitter.repository.*;
import exception.InvalidOrderStatusException;
import exception.ParticipantAlreadyExistsException;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        User creator = new User();
        creator.setId(1L);

        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(creator);
        order.setStatus(OrderStatus.CLOSED);

        when(accessService.getOrderByIdIfCreator(orderId, creator))
                .thenReturn(order);

        assertThrows(InvalidOrderStatusException.class, () -> {
            orderService.closeOrder(orderId, creator);
        });
    }

    @Test
    void shouldDeleteOrderSuccessfully() {
        User creator = new User();
        creator.setId(1L);

        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setCreatedBy(creator);
        order.setStatus(OrderStatus.OPEN);

        when(accessService.getOrderByIdIfCreator(orderId, creator))
                .thenReturn(order);

        // Act
        orderService.deleteOrder(orderId, creator);

        // Assert
        verify(orderRepository).delete(order);
    }

    @Test
    void shouldAssignItemSuccessfully() {
        User currentUser = new User();
        currentUser.setId(2L);

        Long orderId = 1L;
        Long itemId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setStatus(OrderStatus.OPEN);

        Item item = new Item();
        item.setId(itemId);
        item.setName("Pizza");
        item.setPrice(new BigDecimal("4.00"));
        item.setQuantity(1);
        item.setOrder(order);

        // User for each participant
        User user1 = new User();
        user1.setName("Kevin");

        User user2 = new User();
        user2.setName("Ali");

        Participant participant1 = new Participant();
        participant1.setId(10L);
        participant1.setOrder(order);
        participant1.setUser(user1);

        Participant participant2 = new Participant();
        participant2.setId(11L);
        participant2.setOrder(order);
        participant2.setUser(user2);

        AssignRequestDTO assignRequestDTO = new AssignRequestDTO();
        List<Long> participantIds = new ArrayList<>();
        participantIds.add(10L);
        participantIds.add(11L);
        assignRequestDTO.setParticipantIds(participantIds);

        assignRequestDTO.setParticipantIds(participantIds);

        when(accessService.getOrderByIdIfParticipant(orderId, currentUser))
                .thenReturn(order);
        when(itemRepository.findByIdAndOrder(itemId, order))
                .thenReturn(Optional.of(item));
        when(participantRepository.findByIdAndOrder(10L, order))
                .thenReturn(Optional.of(participant1));
        when(participantRepository.findByIdAndOrder(11L, order))
                .thenReturn(Optional.of(participant2));

        // Neither participant is already assigned
        when(itemParticipantRepository.existsByItemAndParticipant(item, participant1))
                .thenReturn(false);
        when(itemParticipantRepository.existsByItemAndParticipant(item, participant2))
                .thenReturn(false);

        // Mock save to return the entity (common pattern even if result isn't used)
        when(itemParticipantRepository.save(any(ItemParticipant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AssignResponseDTO result = orderService.assignItem(orderId, itemId, assignRequestDTO, currentUser);

        // Assert
        assertNotNull(result);
        assertEquals(itemId,result.getItemId());
        assertEquals("Pizza", result.getItemName());
        assertEquals(new BigDecimal("4.00"), result.getTotalPrice());
        assertEquals(2, result.getShares().size());

        BigDecimal expectedShare = new BigDecimal("2.00");
        result.getShares().forEach(share ->
                assertEquals(expectedShare, share.getShare()));

        verify(itemParticipantRepository, times(2)).save(any(ItemParticipant.class));
    }
}
