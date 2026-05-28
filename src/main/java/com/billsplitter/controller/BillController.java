package com.billsplitter.controller;

import com.billsplitter.dto.bill.BillResponseDTO;
import com.billsplitter.model.User;
import com.billsplitter.service.BillService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/{orderId}/bill")
    public BillResponseDTO generateBill(@PathVariable Long orderId) {
        User currentUser = getCurrentUser();
        return billService.generateBill(orderId, currentUser);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
