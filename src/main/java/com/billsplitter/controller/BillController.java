package com.billsplitter.controller;

import com.billsplitter.dto.bill.BillResponseDTO;
import com.billsplitter.model.User;
import com.billsplitter.service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Bill")
@RequestMapping("/orders")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @Operation(summary = "Generate Bill")
    @GetMapping("/{orderId}/bill")
    public BillResponseDTO generateBill(@PathVariable Long orderId) {
        User currentUser = getCurrentUser();
        return billService.generateBill(orderId, currentUser);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
