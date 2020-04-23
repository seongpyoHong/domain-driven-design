package com.sphong.app;

import org.springframework.stereotype.Component;

import java.util.Set;

public interface OrderRepository {
    Set<Order> findByCustomer(Customer customer);
    Set<Order> findAll();
    Order delete(String orderNumber);
    void save(Order order);
}
