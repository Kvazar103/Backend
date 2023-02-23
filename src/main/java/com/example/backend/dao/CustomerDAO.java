package com.example.backend.dao;

import com.example.backend.models.Customer;

import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerDAO extends JpaRepository<Customer,Integer> {
    Customer findCustomerByLogin(String login);

    Boolean existsCustomerByLogin(String login);
}
