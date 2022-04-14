package io.github.raitraidma.microservice.order.service;

import io.github.raitraidma.microservice.order.dto.AccountResponseDto;
import io.github.raitraidma.microservice.order.dto.OrderDto;
import io.github.raitraidma.microservice.order.dto.ProductResponseDto;
import io.github.raitraidma.microservice.order.model.ProductOrder;
import io.github.raitraidma.microservice.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public OrderService(final OrderRepository orderRepository,
                        final RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public OrderDto createOrder(Long accountId, Long productId) {
        ProductOrder order = saveNewOrder(accountId, productId);

        ProductResponseDto productResponseDto = restTemplate.postForObject(
                "http://localhost:8093/products/{productId}/order",
                null,
                ProductResponseDto.class,
                Map.of("productId", productId)
        );
        log.trace("OrderService.createOrder ProductResponseDto: {}", productResponseDto);

        AccountResponseDto accountResponseDto = restTemplate.postForObject(
                "http://localhost:8091/accounts/{accountId}/withdraw?amount={amount}",
                null,
                AccountResponseDto.class,
                Map.of("accountId", accountId, "amount", productResponseDto.getPrice())
        );
        log.trace("OrderService.createOrder AccountResponseDto: {}", accountResponseDto);

        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setAccountId(order.getAccountId());
        orderDto.setProductId(order.getProductId());
        return orderDto;
    }

    private ProductOrder saveNewOrder(Long accountId, Long productId) {
        ProductOrder order = new ProductOrder();
        order.setAccountId(accountId);
        order.setProductId(productId);

        order = orderRepository.save(order);
        return order;
    }
}
