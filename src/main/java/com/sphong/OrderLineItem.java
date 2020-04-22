package com.sphong;

public class OrderLineItem {
    private Product product;
    private Integer quantity;

    private ProductRepository productRepository = new ProductRepositoryImpl();

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
