package com.billsplitter.repository;

import com.billsplitter.model.Order;
import com.billsplitter.model.User;
import com.billsplitter.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCreatedBy(User createdBy);
    List<Order> findByCreatedByAndStatus(User createdBy, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdBy = :user " +
            "OR EXISTS (SELECT p FROM Participant p WHERE p.order = o AND p.user = :user)")
    List<Order> findAllByUser(@Param("user") User user);

    // For access service - verifies if is creator
    Optional<Order> findByIdAndCreatedBy(Long id, User createdBy);

    // For access service - verifies if is participant
    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND (o.createdBy = :user " +
            "OR EXISTS (SELECT p FROM Participant p WHERE p.order = o AND p.user = :user))")
    Optional<Order> findByIdAndUser(@Param("orderId") Long orderId, @Param("user") User user);
}