package com.sphong.app;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable(autowire = Autowire.BY_TYPE, value = "orderLineItem", preConstruction = true)
public class OrderLineItem {
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
}
