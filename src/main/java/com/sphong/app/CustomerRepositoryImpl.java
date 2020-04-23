package com.sphong.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("customerRepository")
public class CustomerRepositoryImpl implements CustomerRepository{
    private final Register register;

    public CustomerRepositoryImpl(Register register) {
        this.register = register;
    }

    public void save(Customer customer) {
        register.add(Customer.class, customer);
    }
    public Customer find(String identity) {
       return (Customer) register.get(Customer.class, identity);
    }
}
