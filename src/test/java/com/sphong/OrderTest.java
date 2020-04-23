package com.sphong;

import com.sphong.app.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrderTest {
    private Customer customer;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Register register;

    @BeforeEach
    public void setUp() {
        register.init();
        productRepository.save(new Product("상품1", 1000));
        productRepository.save(new Product("상품2", 5000));
        customer = new Customer("CUST-01", "sphong", "korea", 200000);
    }

    @Test
    public void testOrderPrice() throws OrderLimitExceededException {
        Order order = customer.newOrder("CUST-01-ORDER-01")
                .with("상품1",10)
                .with("상품2",20);
        orderRepository.save(order);
        assertEquals(new Money(110000), order.getTotalPrice());
    }

}