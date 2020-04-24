package com.sphong.app;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Money {
    private Integer amount;
    public Money() {
    }
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
        return new Money(this.amount);
    }

    public Money multiply(Integer quantity) {
        return new Money(this.amount * quantity);
    }

    public boolean isGreaterThan(Money limitPrice) {
        System.out.println("Parameter :" + limitPrice.amount);
        System.out.println("This : " + this.amount);
        return limitPrice.amount < this.amount;
    }

    public Integer getAmount() {
        return amount;
    }
}