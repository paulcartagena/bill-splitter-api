package com.billsplitter.repository;

import com.billsplitter.model.Item;
import com.billsplitter.model.ItemParticipant;
import com.billsplitter.model.Order;
import com.billsplitter.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemParticipantRepository extends JpaRepository<ItemParticipant, Long> {
    boolean existsByItemAndParticipant(Item item, Participant participant);
    List<ItemParticipant> findByParticipant(Participant participant);
    List<ItemParticipant> findByParticipant_Order(Order order);
}
