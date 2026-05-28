package com.billsplitter.repository;

import com.billsplitter.model.Item;
import com.billsplitter.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOrder(Order order);
    Optional<Item> findByIdAndOrder(Long itemId, Order order);
}
