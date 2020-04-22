package com.sphong;

public class ProductRepositoryImpl implements ProductRepository {
    @Override
    public void save(Product product) {
        Register.add(Product.class, product);
    }

    @Override
    public Product find(String productName) {
        return (Product) Register.get(Product.class, productName);
    }
}
