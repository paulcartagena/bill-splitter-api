package com.billsplitter.model;

import com.billsplitter.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    private String name;

    @Column(name = "restaurant_name")
    private String restaurantName;
    private BigDecimal tax;
    private BigDecimal tip;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = ("order"), cascade = CascadeType.ALL, orphanRemoval = true)
    List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = ("order"), cascade = CascadeType.ALL, orphanRemoval = true)
    List<Item> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
