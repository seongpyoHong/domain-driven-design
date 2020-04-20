package com.sphong;

import lombok.Getter;

@Getter
public class Customer extends EntryPoint {
    private String number;
    private String name;
    private String address;
    private Long mileages;
    private Money limitPrice;

    public Customer(String number, String name, String address, Integer limitPrice) {
        super(number);
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

    public Order newOrder(String orderId) {
        return Order.order(orderId, this);
    }

    public boolean isExceedLimitPrice(Money money) {
        return money.isGreaterThan(limitPrice);
    }
}
