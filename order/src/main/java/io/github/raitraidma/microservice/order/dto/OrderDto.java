package io.github.raitraidma.microservice.order.dto;

import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private Long productId;
    private Long accountId;
}
