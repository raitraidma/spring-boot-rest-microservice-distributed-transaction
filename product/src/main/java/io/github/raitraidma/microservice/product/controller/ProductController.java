package io.github.raitraidma.microservice.product.controller;

import io.github.raitraidma.microservice.product.dto.ProductOrderDto;
import io.github.raitraidma.microservice.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("products")
public class ProductController {
    private final ProductService productService;

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("{productId}/order")
    public ProductOrderDto createOrder(@PathVariable Long productId) {
        return productService.orderProduct(productId);
    }
}
