package com.sphong;

import com.sphong.app.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OrdersTest {
    private Customer customer;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        productRepository.save(new Product("상품1", 1000));
        productRepository.save(new Product("상품2", 5000));
        customer = new Customer("CUST-01", "sphong", "korea", 200000);
        customerRepository.save(customer);
    }
//
//    @Test
//    public void testProductRepository () {
//        Product product = productRepository.findByName("상품1").get();
//        assertEquals(product.getName(), "상품1");
//    }
    @Test
    public void testOrderPrice() throws OrderLimitExceededException {
        Customer customer1 = customerRepository.findByName("sphong").get();
        Orders orders = customer1.newOrder("CUST-01-ORDER-01")
                .with("상품1",10)
                .with("상품2",20);

        orderRepository.save(orders);
        assertEquals(new Money(110000), orders.getTotalPrice());
    }

}