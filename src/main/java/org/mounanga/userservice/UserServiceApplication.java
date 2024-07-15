package org.mounanga.userservice;

import lombok.extern.slf4j.Slf4j;
import org.mounanga.userservice.entity.Role;
import org.mounanga.userservice.entity.User;
import org.mounanga.userservice.enums.Gender;
import org.mounanga.userservice.repository.RoleRepository;
import org.mounanga.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }


    @Bean
    CommandLineRunner start(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if(!roleRepository.existsBy()) {
                roleRepository.save(Role.builder().name("USER").description("USER").build());
                roleRepository.save(Role.builder().name("MODERATOR").description("MODERATOR").build());
                roleRepository.save(Role.builder().name("ADMIN").description("ADMIN").build());
                roleRepository.save(Role.builder().name("SUPER_ADMIN").description("SUPER_ADMIN").build());
                log.info("default roles successfully created");
            }
            if(!userRepository.existsBy()) {
                try{
                    String pwd = UUID.randomUUID().toString();
                    User user = new User();
                    user.setUsername("admin");
                    user.setPassword(passwordEncoder.encode(pwd));
                    user.setFirstname("SUPER ADMIN");
                    user.setLastname("SUPER ADMIN");
                    user.setEmail("admin@admin.com");
                    user.setPlaceOfBirth("SYSTEM.");
                    user.setNationality("WORLD");
                    user.setDateOfBirth(LocalDate.now());
                    user.setGender(Gender.M);
                    user.setNip("SYSTEM");
                    user.setPhone("SYSTEM");
                    user.setEnabled(true);
                    user.setPasswordNeedsToBeChanged(true);
                    User savedUser = userRepository.save(user);
                    List<Role> roles = roleRepository.findAll();
                    for(Role role : roles) {
                        savedUser.addRole(role);
                    }
                    userRepository.save(savedUser);
                    log.info("*****************************User added successfully*********************");
                    log.info("DEFAULT PASSWORD :{}", pwd);
                    log.info("***************************User added successfully******************");
                }catch(Exception e){
                   log.warn(e.getMessage());
                   log.error(e.getLocalizedMessage());
                }
            }
        };
    }

}
