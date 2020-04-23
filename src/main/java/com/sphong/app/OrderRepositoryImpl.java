package com.sphong.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component("orderRepository")
public class OrderRepositoryImpl implements OrderRepository {
    @Autowired
    private Register register;

    public Set<Order> findByCustomer(Customer customer) {
        Set<Order> results = new HashSet<Order>();
        for (Order order : findAll()) {
            if (order.idOrderBy(customer)) {
                results.add(order);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public Set<Order> findAll() {
        return new HashSet<Order>((Collection<? extends Order>) register.getAll(Order.class));
    }

    public Order delete(String orderNumber) {
        return (Order) register.delete(Order.class, orderNumber);
    }
    public void save(Order order) {
        register.add(Order.class, order);
    }

}
