package com.multitenacydemo.controller;

import com.multitenacydemo.entity.User;
import com.multitenacydemo.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }


    // Requires Header: X-Tenant-ID
    @GetMapping("/")
    public List<User> all() {
        return service.getAllUsers();
    }

    // Requires Header: X-Tenant-ID
    @PostMapping("/")
    public User create(@RequestBody Map<String, String> body) {
        return service.createUser(body.get("name"));
    }
}
