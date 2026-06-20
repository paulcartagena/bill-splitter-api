package com.billsplitter.service;

import com.billsplitter.model.Order;
import com.billsplitter.model.User;
import com.billsplitter.repository.OrderRepository;
import exception.OrderNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccessServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AccessService accessService;

    @Test
    void shouldReturnOrderIfUserIsParticipant() {
        User user = new User();
        user.setId(2L);

        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findByIdAndUser(order.getId(), user))
                .thenReturn(Optional.of(order));

        // Act
        Order result = accessService.getOrderByIdIfParticipant(order.getId(), user);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
    }

    @Test
    void shouldThrowIfUserNotParticipant() {
        User user = new User();
        user.setId(2L);

        when(orderRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            accessService.getOrderByIdIfParticipant(1L, user);
        });
    }

    @Test
    void shouldReturnOrderIfUserIsCreator() {
        User creator = new User();
        creator.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setCreatedBy(creator);

        when(orderRepository.findByIdAndCreatedBy(1L, creator))
                .thenReturn(Optional.of(order));

        // Act
        Order result = accessService.getOrderByIdIfCreator(order.getId(), creator);

        // Assert
        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(creator, order.getCreatedBy());
    }

    @Test
    void shouldThrowIfUserNotCreator() {
        User user = new User();
        user.setId(2L);

        User creator = new User();
        creator.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setCreatedBy(creator);

        when(orderRepository.findByIdAndCreatedBy(order.getId(), creator))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            accessService.getOrderByIdIfCreator(order.getId(), creator);
        });
    }
}
