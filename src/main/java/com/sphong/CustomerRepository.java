package com.sphong;

public class CustomerRepository {
    public void save(Customer customer) {
        Register.add(Customer.class, customer);
    }
    public Customer find(String identity) {
       return (Customer) Register.get(Customer.class, identity);
    }
}
