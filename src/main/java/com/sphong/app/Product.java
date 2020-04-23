package com.sphong.app;

public class Product extends EntryPoint {
    private String name;
    private Money price;

    public Product(String name, Integer price) {
        super(name);
        this.name = name;
        this.price = new Money(price);
    }

    public Product(String name, Money price) {
        super(name);
        this.name = name;
        this.price = price;
    }

    public Money getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
