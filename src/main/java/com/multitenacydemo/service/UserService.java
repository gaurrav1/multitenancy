package com.multitenacydemo.service;

import com.multitenacydemo.entity.User;
import com.multitenacydemo.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User createUser(String name) {
        User user = new User();
        user.setName(name);
        return repo.save(user);
    }
}
