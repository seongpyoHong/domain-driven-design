package com.sphong.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    Set<Orders> findByCustomer(Customer customer);
}
