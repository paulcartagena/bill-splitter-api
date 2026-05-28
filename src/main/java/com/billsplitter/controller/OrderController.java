package com.billsplitter.controller;

import com.billsplitter.dto.item.AssignRequestDTO;
import com.billsplitter.dto.item.AssignResponseDTO;
import com.billsplitter.dto.item.ItemRequestDTO;
import com.billsplitter.dto.item.ItemResponseDTO;
import com.billsplitter.dto.order.OrderRequestDTO;
import com.billsplitter.dto.order.OrderResponseDTO;
import com.billsplitter.dto.participant.ParticipantRequestDTO;
import com.billsplitter.dto.participant.ParticipantSummaryDTO;
import com.billsplitter.model.User;
import com.billsplitter.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Order Endpoints
    @GetMapping
    public List<OrderResponseDTO> getAllOrders() {
        User currentUser = getCurrentUser();
        return orderService.findAllOrders(currentUser);
    }

    @GetMapping("/{orderId}")
    public OrderResponseDTO getOrderById(@PathVariable Long orderId) {
        User currentUser = getCurrentUser();
        return orderService.findOrderById(orderId, currentUser);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        User currentUser = getCurrentUser();
        return orderService.createOrder(orderRequestDTO, currentUser);
    }

    @PutMapping("/{orderId}")
    public OrderResponseDTO updateOrder(@PathVariable Long orderId,
                                        @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        User creator = getCurrentUser();
        return orderService.updateOrder(orderId, orderRequestDTO, creator);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long orderId) {
        User creator = getCurrentUser();
        orderService.deleteOrder(orderId, creator);
    }

    // Status Endpoints
    @PatchMapping("/{orderId}/close")
    public OrderResponseDTO closeOrder(@PathVariable Long orderId) {
        User creator = getCurrentUser();
        return orderService.closeOrder(orderId, creator);
    }

    @PatchMapping("/{orderId}/reopen")
    public OrderResponseDTO reopenOrder(@PathVariable Long orderId) {
        User creator = getCurrentUser();
        return orderService.reOpenOrder(orderId, creator);
    }

    @PatchMapping("/{orderId}/pay")
    public OrderResponseDTO markAsPaidOrder(@PathVariable Long orderId) {
        User creator = getCurrentUser();
        return orderService.markAsPaid(orderId, creator);
    }

    // Participant Endpoints
    @PostMapping("/{orderId}/participants")
    public ParticipantSummaryDTO addParticipant(@PathVariable Long orderId,
                                                @Valid @RequestBody ParticipantRequestDTO participantRequestDTO) {
        User creator = getCurrentUser();
        return orderService.addParticipant(orderId, participantRequestDTO.getEmail(), creator);
    }

    @DeleteMapping("/{orderId}/participants")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeParticipant(@PathVariable Long orderId,
                                  @RequestParam String email) {
        User creator = getCurrentUser();
        orderService.removeParticipant(orderId, email, creator);
    }

    // Item Endpoints
    @GetMapping("/{orderId}/items")
    public List<ItemResponseDTO> getAllItems(@PathVariable Long orderId) {
        User currentUser = getCurrentUser();
        return orderService.getAllItems(orderId, currentUser);
    }

    @PostMapping("/{orderId}/items")
    public ItemResponseDTO addItem(@PathVariable Long orderId,
                                   @Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        User currentUser = getCurrentUser();
        return orderService.addItem(orderId, itemRequestDTO, currentUser);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public ItemResponseDTO updateItem(@PathVariable Long orderId,
                                      @PathVariable Long itemId,
                                      @Valid @RequestBody ItemRequestDTO itemRequestDTO) {
        User currentUser = getCurrentUser();
        return orderService.updateItem(orderId, itemId, itemRequestDTO, currentUser);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long orderId,
                           @PathVariable Long itemId) {
        User currentUser = getCurrentUser();
        orderService.removeItem(orderId, itemId, currentUser);
    }

    @PostMapping("/{orderId}/items/{itemId}/assignItem")
    public AssignResponseDTO assignItem(@PathVariable Long orderId,
                                        @PathVariable Long itemId,
                                        @Valid @RequestBody AssignRequestDTO assignRequestDTO) {
        User currentUser = getCurrentUser();
        return orderService.assignItem(orderId, itemId, assignRequestDTO, currentUser);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
