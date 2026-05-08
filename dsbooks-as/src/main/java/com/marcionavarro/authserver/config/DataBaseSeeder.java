package com.marcionavarro.authserver.config;

import com.marcionavarro.authserver.entities.UserEntity;
import com.marcionavarro.authserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataBaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            return;
        }
        seedUser("a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                 "lita@email.com",
                 "Angelita Martins",
                 "https://i.pravatar.cc/300?img=1");

        seedUser("b2c3d4e5-f6a7-8901-bcde-f12345678901",
                 "mn@email.com",
                 "Marcio Navarro",
                 "https://i.pravatar.cc/300?img=2");
    }

    private void seedUser(String sub, String email, String name, String picture) {
        UserEntity user = new UserEntity();
        user.setSub(sub);
        user.setEmail(email);
        user.setEmailVerified(false);
        user.setPassword("{noop}admin123");
        user.setName(name);
        user.setPicture(picture);
        userRepository.save(user);
    }
}
