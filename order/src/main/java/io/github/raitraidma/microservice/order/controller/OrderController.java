package io.github.raitraidma.microservice.order.controller;

import io.github.raitraidma.microservice.order.dto.OrderDto;
import io.github.raitraidma.microservice.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDto> getOrders() {
        return orderService.getOrders();
    }

    @PostMapping
    public OrderDto createOrder(@RequestParam Long accountId, @RequestParam Long productId) {
        return orderService.createOrder(accountId, productId);
    }
}
