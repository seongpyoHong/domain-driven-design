package com.sphong.app;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Order {
    private Long id;
    private String orderId;
    private Set<OrderLineItem> lineItems = new HashSet<>();
    private Customer customer;

    public static Order order(String orderId, Customer customer) {
        return new Order(orderId, customer);
    }

    Order(String orderId, Customer customer) {
        this.orderId = orderId;
        this.customer = customer;
    }

    public Order with(String productName, int quantity) throws OrderLimitExceededException {
        return with(new OrderLineItem(productName, quantity));
    }

    private Order with(OrderLineItem lineItem) throws OrderLimitExceededException {
        if (isExceedLimit(customer, lineItem)) {
            throw new OrderLimitExceededException();
        }

        for(OrderLineItem item : lineItems) {
            if (item.isProductEqual(lineItem)) {
                item.merge(lineItem);
                return this;
            }
        }
        lineItems.add(lineItem);
        return this;
    }

    public Money getTotalPrice() {
        Money result = new Money(0);
        for(OrderLineItem item : lineItems) {
            result = result.add(item.getPrice());
        }

        return result;
    }

    private boolean isExceedLimit(Customer customer, OrderLineItem lineItem) {
        return customer.isExceedLimitPrice(getTotalPrice().add(lineItem.getPrice()));
    }

    public boolean idOrderBy(Customer customer) {
        return this.customer == customer;
    }
    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
