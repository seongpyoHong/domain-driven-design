package com.sphong.app;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Objects;

@Configurable(autowire = Autowire.BY_TYPE, value = "orderLineItem", preConstruction = true)
public class OrderLineItem {
    private Long id;
    private Product product;
    private Integer quantity;

    @Autowired
    private ProductRepository productRepository;

    public OrderLineItem(String productName, Integer quantity) {
        this.product = productRepository.find(productName);
        this.quantity = quantity;
    }

    public Money getPrice() {
        return product.getPrice().multiply(quantity);
    }

    public Product getProduct() {
        return product;
    }

    public boolean isProductEqual(OrderLineItem lineItem) {
        return this.product == lineItem.product;
    }

    public OrderLineItem merge(OrderLineItem lineItem) {
        quantity += lineItem.quantity;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderLineItem that = (OrderLineItem) o;
        return Objects.equals(product, that.product) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity);
    }
}
