package com.sphong.app;

import org.springframework.stereotype.Repository;

public interface CustomerRepository {
    void save(Customer customer);
    Customer find(String identity);
}
