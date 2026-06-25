package com.billsplitter.service;

import com.billsplitter.dto.UserSummaryDTO;
import com.billsplitter.dto.item.AssignRequestDTO;
import com.billsplitter.dto.item.AssignResponseDTO;
import com.billsplitter.dto.item.ItemRequestDTO;
import com.billsplitter.dto.item.ItemResponseDTO;
import com.billsplitter.dto.order.OrderRequestDTO;
import com.billsplitter.dto.order.OrderResponseDTO;
import com.billsplitter.dto.participant.ParticipantShareDTO;
import com.billsplitter.dto.participant.ParticipantSummaryDTO;
import com.billsplitter.exception.*;
import com.billsplitter.model.*;
import com.billsplitter.model.enums.OrderRole;
import com.billsplitter.model.enums.OrderStatus;
import com.billsplitter.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccessService accessService;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final ItemRepository itemRepository;
    private final ItemParticipantRepository itemParticipantRepository;

    public OrderService(OrderRepository orderRepository,
                        AccessService accessService,
                        UserRepository userRepository,
                        ParticipantRepository participantRepository,
                        ItemRepository itemRepository,
                        ItemParticipantRepository itemParticipantRepository) {
        this.orderRepository = orderRepository;
        this.accessService = accessService;
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.itemRepository = itemRepository;
        this.itemParticipantRepository = itemParticipantRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAllOrders(User currentUser) {
        return orderRepository.findAllByUser(currentUser)
                .stream()
                .map(order -> buildOrderResponse(order, currentUser))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findOrderById(Long orderId, User currentUser) {
        Order order = accessService.getOrderByIdIfParticipant(orderId, currentUser);
        return buildOrderResponse(order, currentUser);
    }

    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO, User creator) {
        Order order = new Order();
        order.setCreatedBy(creator);
        order.setName(orderRequestDTO.getName());
        order.setRestaurantName(orderRequestDTO.getRestaurantName());
        order.setTax(orderRequestDTO.getTax());
        order.setTip(orderRequestDTO.getTip());
        order.setStatus(OrderStatus.OPEN);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Add the creator as a participant
        Participant creatorParticipant = new Participant();
        creatorParticipant.setUser(creator);
        creatorParticipant.setOrder(savedOrder);
        participantRepository.save(creatorParticipant);

        return buildOrderResponse(savedOrder, creator);
    }

    public OrderResponseDTO updateOrder(Long orderId, OrderRequestDTO orderRequestDTO, User creator) {
        Order order = accessService.getOrderByIdIfCreator(orderId, creator);

        order.setName(orderRequestDTO.getName());
        order.setRestaurantName(orderRequestDTO.getRestaurantName());
        order.setTax(orderRequestDTO.getTax());
        order.setTip(orderRequestDTO.getTip());

        Order updatedOrder = orderRepository.save(order);
        return buildOrderResponse(updatedOrder, creator);
    }

    public void deleteOrder(Long orderId, User creator) {
        Order order = accessService.getOrderByIdIfCreator(orderId, creator);
        validateOrderIsOpen(order);
        orderRepository.delete(order);
    }

    public OrderResponseDTO closeOrder(Long orderId, User creator) {
        Order order = accessService.getOrderByIdIfCreator(orderId, creator);
        validateOrderIsOpen(order);
        order.setStatus(OrderStatus.CLOSED);
        return buildOrderResponse(orderRepository.save(order), creator);
    }

    public OrderResponseDTO reOpenOrder(Long orderId, User creator) {
        Order order = accessService.getOrderByIdIfCreator(orderId, creator);
        validateOrderIsClosed(order);
        order.setStatus(OrderStatus.OPEN);
        return buildOrderResponse(orderRepository.save(order), creator);
    }

    public OrderResponseDTO markAsPaid(Long orderId, User creator) {
        Order order = accessService.getOrderByIdIfCreator(orderId, creator);
        validateOrderIsClosed(order);
        order.setStatus(OrderStatus.PAID);
        return buildOrderResponse(orderRepository.save(order), creator);
    }

    public ParticipantSummaryDTO addParticipant(Long orderId, String userEmail, User creator) {
        Order order = accessService.getOrderByIdIfCreator(orderId, creator);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        validateOrderIsOpen(order);

        if (userEmail.equals(creator.getEmail())) {
            throw new ParticipantAlreadyExistsException("You cannot add yourself as a participant.");
        }

        boolean alreadyParticipant = participantRepository.existsByOrderAndUser(order, user);
        if (alreadyParticipant) {
            throw new ParticipantAlreadyExistsException("User is already a participant.");
        }

        Participant participant = new Participant();
        participant.setUser(user);
        participant.setOrder(order);

        Participant saved = participantRepository.save(participant);
        return buildParticipantSummary(saved);
    }

    public void removeParticipant(Long orderId, String userEmail, User creator) {
        Order order = accessService.getOrderByIdIfCreator(orderId, creator);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        validateOrderIsOpen(order);

        if (userEmail.equals(creator.getEmail())) {
            throw new CannotRemoveCreatorException("You cannot remove yourself as a participant.");
        }

        Participant participant = participantRepository.findByOrderAndUser(order, user)
                .orElseThrow(() -> new ParticipantNotFoundException("Participant not found."));

        participantRepository.delete(participant);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getAllItems(Long orderId, User currentUser) {
        Order order = accessService.getOrderByIdIfParticipant(orderId, currentUser);
        return itemRepository.findAllByOrder(order)
                .stream()
                .map(this::buildItemResponse)
                .toList();
    }

    public ItemResponseDTO addItem(Long orderId, ItemRequestDTO itemRequestDTO, User currentUser) {
        Order order = accessService.getOrderByIdIfParticipant(orderId, currentUser);
        validateOrderIsOpen(order);

        Item item = new Item();
        item.setOrder(order);
        item.setName(itemRequestDTO.getName());
        item.setPrice(itemRequestDTO.getPrice());
        item.setQuantity(itemRequestDTO.getQuantity());

        Item addedItem = itemRepository.save(item);
        return buildItemResponse(addedItem);
    }

    public ItemResponseDTO updateItem(Long orderId, Long itemId, ItemRequestDTO itemRequestDTO, User currentUser) {
        Order order = accessService.getOrderByIdIfParticipant(orderId, currentUser);
        validateOrderIsOpen(order);

        Item item = itemRepository.findByIdAndOrder(itemId, order)
                        .orElseThrow(() -> new ItemNotFoundException("Item not found."));

        item.setName(itemRequestDTO.getName());
        item.setPrice(itemRequestDTO.getPrice());
        item.setQuantity(itemRequestDTO.getQuantity());

        Item updatedItem = itemRepository.save(item);
        return buildItemResponse(updatedItem);
    }

    public void removeItem(Long orderId, Long itemId, User currentUser) {
        Order order = accessService.getOrderByIdIfParticipant(orderId, currentUser);
        validateOrderIsOpen(order);

        Item item = itemRepository.findByIdAndOrder(itemId, order)
                .orElseThrow(() -> new ItemNotFoundException("Item not found."));

        itemRepository.delete(item);
    }

    public AssignResponseDTO assignItem(Long orderId, Long itemId, AssignRequestDTO assignRequestDTO, User currentUser) {
        Order order = accessService.getOrderByIdIfParticipant(orderId, currentUser);
        validateOrderIsOpen(order);

        Item item = itemRepository.findByIdAndOrder(itemId, order)
                .orElseThrow(() -> new ItemNotFoundException("Item not found."));

        BigDecimal total = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        BigDecimal share = total.divide(BigDecimal.valueOf(assignRequestDTO.getParticipantIds().size()), 2, RoundingMode.HALF_UP);

        List<ParticipantShareDTO> shares = new ArrayList<>();

        for (Long participantId : assignRequestDTO.getParticipantIds()) {
            Participant participant= participantRepository.findByIdAndOrder(participantId, order)
                    .orElseThrow(() -> new ParticipantNotFoundException("Participant not found"));

            boolean alreadyAssigned = itemParticipantRepository.existsByItemAndParticipant(item, participant);
            if (alreadyAssigned) {
                throw new ItemAlreadyAssignedException("Participant is already assigned to this item");
            }

            ItemParticipant itemParticipant = new ItemParticipant();
            itemParticipant.setItem(item);
            itemParticipant.setParticipant(participant);
            itemParticipant.setShare(share);
            itemParticipantRepository.save(itemParticipant);

            shares.add(new ParticipantShareDTO(
                    participant.getId(),
                    participant.getUser().getName(),
                    share
            ));
        }

        return new AssignResponseDTO(item.getId(), item.getName(), total, shares);
    }

    private void validateOrderIsOpen(Order order) {
        if (order.getStatus() != OrderStatus.OPEN) {
            throw new InvalidOrderStatusException("Order must be OPEN.");
        }
    }

    private void validateOrderIsClosed(Order order) {
        if (order.getStatus() != OrderStatus.CLOSED) {
            throw new InvalidOrderStatusException("Order must be CLOSED.");
        }
    }

    private OrderResponseDTO buildOrderResponse(Order order, User currentUser) {
        OrderRole role = order.getCreatedBy().getId().equals(currentUser.getId())
                ? OrderRole.CREATOR
                : OrderRole.PARTICIPANT;

        return new OrderResponseDTO(
                order.getId(),
                buildUserSummary(order.getCreatedBy()),
                order.getName(),
                order.getRestaurantName(),
                order.getTax(),
                order.getTip(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getParticipants()
                        .stream()
                        .map(this::buildParticipantSummary)
                        .toList(),
                role
        );
    }

    private ItemResponseDTO buildItemResponse(Item item) {
        return new ItemResponseDTO(
                item.getId(),
                item.getOrder().getId(),
                item.getName(),
                item.getPrice(),
                item.getQuantity()
        );
    }

    private UserSummaryDTO buildUserSummary(User user) {
        return new UserSummaryDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    private ParticipantSummaryDTO buildParticipantSummary(Participant participant) {
        return new ParticipantSummaryDTO(
                participant.getId(),
                participant.getUser().getId(),
                participant.getUser().getName(),
                participant.getUser().getEmail(),
                OrderRole.PARTICIPANT
        );
    }
}
