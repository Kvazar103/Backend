package com.example.backend.dao;

import com.example.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Integer> {
    User findUserByLogin(String login);
}
