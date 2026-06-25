package com.billsplitter.service;

import com.billsplitter.model.Order;
import com.billsplitter.model.User;
import com.billsplitter.repository.OrderRepository;
import com.billsplitter.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    private final OrderRepository orderRepository;

    public AccessService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order getOrderByIdIfCreator(Long orderId, User creator) {
       return orderRepository.findByIdAndCreatedBy(orderId, creator)
                .orElseThrow(() -> new OrderNotFoundException("Order not found."));
    }

    public Order getOrderByIdIfParticipant(Long orderId, User user) {
        return orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new OrderNotFoundException("Order not found."));
    }
}