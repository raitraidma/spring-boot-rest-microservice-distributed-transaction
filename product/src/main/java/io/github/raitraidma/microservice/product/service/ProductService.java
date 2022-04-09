package io.github.raitraidma.microservice.product.service;

import io.github.raitraidma.microservice.product.dto.ProductOrderDto;
import io.github.raitraidma.microservice.product.model.Product;
import io.github.raitraidma.microservice.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductOrderDto orderProduct(Long productId) {
        Product product = productRepository.getById(productId);
        product.setAmount(product.getAmount() - 1);
        product = productRepository.save(product);

        ProductOrderDto productOrderDto = new ProductOrderDto();
        productOrderDto.setId(product.getId());
        productOrderDto.setPrice(product.getPrice());
        return productOrderDto;
    }
}
