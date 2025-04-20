package com.multitenacydemo;

import com.multitenacydemo.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DemoCommandLiner {

    @Bean
    public CommandLineRunner loadUsers(UserRepository userRepo) {
        return args -> {
            System.out.println("Loading users...");
        };
    }
}
