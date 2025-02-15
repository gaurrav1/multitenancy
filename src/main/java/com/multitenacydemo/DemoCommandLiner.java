package com.multitenacydemo;

import com.multitenacydemo.config.TenantContext;
import com.multitenacydemo.model.User;
import com.multitenacydemo.repo.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoCommandLiner {

    @Bean
    public CommandLineRunner loadUsers(UserRepo userRepo) {
        return args -> {
            TenantContext.setCurrentTenant("TENANT_DB_1");
            List<User> users = userRepo.findAll();
            System.out.println();
            TenantContext.setCurrentTenant("TENANT_DB_2");
            users.addAll(userRepo.findAll());
            for (User user : users) {
                System.out.println(user.getUserName());
            }
        };
    }
}
