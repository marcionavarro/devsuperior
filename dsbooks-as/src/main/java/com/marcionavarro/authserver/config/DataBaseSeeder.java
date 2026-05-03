package com.marcionavarro.authserver.config;

import com.marcionavarro.authserver.entities.UserEntity;
import com.marcionavarro.authserver.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataBaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
                if(userRepository.count() > 0) {
                    return;
                }
                seedUser("marcio@email.com", "{noop}admin123");
                seedUser("lita@email.com", "{noop}admin123");
    }

    private void seedUser(String username, String password) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);
    }
}
