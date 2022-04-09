package io.github.raitraidma.microservice.product.repository;

import io.github.raitraidma.microservice.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
