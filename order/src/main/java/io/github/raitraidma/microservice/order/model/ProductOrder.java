package io.github.raitraidma.microservice.order.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "product_id")
    private Long productId;
}
