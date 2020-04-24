package com.sphong.app;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private String name;
    private String address;
    private Long mileages;
    private Money limitPrice;

    public Customer(){
    }
    public Customer(String number, String name, String address, Integer limitPrice) {
        this.number = number;
        this.name = name;
        this.address = address;
        this.limitPrice = new Money(limitPrice);
    }

    public void purchase(Long price) {
        this.mileages += (price / 100L);
    }

    public Boolean isPossibleToPayWithMileage(Long price) {
        return mileages > price;
    }

    public Boolean payWithMileage(Long price) {
        if (isPossibleToPayWithMileage(price)) {
            return false;
        }
        mileages -= price;
        return true;
    }

    public Orders newOrder(String orderId) {
        return Orders.order(orderId, this);
    }

    public boolean isExceedLimitPrice(Money money) {
        return money.isGreaterThan(limitPrice);
    }
}
