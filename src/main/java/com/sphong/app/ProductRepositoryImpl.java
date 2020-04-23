package com.sphong.app;

import org.springframework.stereotype.Component;

@Component("productRepository")
public class ProductRepositoryImpl implements ProductRepository {

    private final Register register;

    public ProductRepositoryImpl(Register register) {
        this.register = register;
    }

    @Override
    public void save(Product product) {
        register.add(Product.class, product);
    }

    @Override
    public Product find(String productName) {
        return (Product) register.get(Product.class, productName);
    }
}
