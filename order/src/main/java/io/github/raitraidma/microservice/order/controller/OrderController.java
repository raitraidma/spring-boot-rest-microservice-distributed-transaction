package io.github.raitraidma.microservice.order.controller;

import io.github.raitraidma.microservice.order.dto.OrderDto;
import io.github.raitraidma.microservice.order.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderDto createOrder(@RequestParam Long accountId, @RequestParam Long productId) {
        return orderService.createOrder(accountId, productId);
    }
}
