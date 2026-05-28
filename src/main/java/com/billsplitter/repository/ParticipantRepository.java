package com.billsplitter.repository;

import com.billsplitter.model.Order;
import com.billsplitter.model.Participant;
import com.billsplitter.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    boolean existsByOrderAndUser(Order order, User user);
    Optional<Participant> findByOrderAndUser(Order order, User user);
    Optional<Participant> findByIdAndOrder(Long id, Order order);
}
