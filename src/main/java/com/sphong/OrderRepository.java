package com.sphong;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OrderRepository {
    public Set<Order> findByCustomer(Customer customer) {
        Set<Order> results = new HashSet<Order>();
        for (Order order : findAll()) {
            if (order.idOrderBy(customer)) {
                results.add(order);
            }
        }
        return results;
    }

    public Set<Order> findAll() {
        return new HashSet<Order>((Collection<? extends Order>) Register.getAll(Order.class));
    }

    public Order delete(String orderNumber) {
        return (Order) Register.delete(Order.class, orderNumber);
    }
}
