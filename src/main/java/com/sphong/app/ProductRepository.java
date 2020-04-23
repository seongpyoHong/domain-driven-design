package com.sphong.app;

import org.springframework.stereotype.Component;

public interface ProductRepository {
    void save(Product product);
    Product find(String productName);
}
