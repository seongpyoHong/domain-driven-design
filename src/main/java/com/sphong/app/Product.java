package com.sphong.app;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Target;

import javax.persistence.*;

@Entity
public class Product{
    @Id
    private String name;

    private Money price;

    public Product() {
    }
    public Product(String name, Integer price) {
        this.name = name;
        this.price = new Money(price);
    }

    public Product(String name, Money price) {
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
