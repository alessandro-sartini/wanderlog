package com.travel.wanderlog.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.travel.wanderlog.model.User;
import com.travel.wanderlog.repository.UserRepository;

@Configuration
@Profile("dev")
public class SeedConfig {

    @Bean
    CommandLineRunner seed(UserRepository users) {
        return args -> {
            if (!users.existsByEmail("demo@travelsage.io")) {
                users.save(User.builder()
                        .email("demo@travelsage.io")
                        .passwordHash("hash_finto")   // in seguito: BCrypt
                        .displayName("Demo User")
                        .build());
            }
        };
    }
}

