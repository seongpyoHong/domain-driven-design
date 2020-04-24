package com.sphong.app;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "ORDER_NAME")
    private String orderId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderLineItem> lineItems = new HashSet<>();

    @ManyToOne
    private Customer customer;

    public static Orders order(String orderId, Customer customer) {
        return new Orders(orderId, customer);
    }

    Orders(String orderId, Customer customer) {
        this.orderId = orderId;
        this.customer = customer;
    }

    public Orders with(String productName, int quantity) throws OrderLimitExceededException {
        return with(new OrderLineItem(productName, quantity));
    }

    private Orders with(OrderLineItem lineItem) throws OrderLimitExceededException {
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
        Orders orders = (Orders) o;
        return orderId.equals(orders.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
