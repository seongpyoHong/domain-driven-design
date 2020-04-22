package com.sphong;

public interface ProductRepository {
    void save(Product product);
    Product find(String productName);
}
