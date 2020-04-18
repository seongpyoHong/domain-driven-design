package com.sphong;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Money {
    private Integer amount;
    public Money(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    public Money add(Money added) {
        this.amount += added.amount;
        return this;
    }
}