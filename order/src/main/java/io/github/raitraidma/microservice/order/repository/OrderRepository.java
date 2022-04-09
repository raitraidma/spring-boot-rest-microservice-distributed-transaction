package io.github.raitraidma.microservice.order.repository;

import io.github.raitraidma.microservice.order.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<ProductOrder, Long> {
}
