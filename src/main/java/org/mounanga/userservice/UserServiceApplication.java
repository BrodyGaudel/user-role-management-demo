package org.mounanga.userservice;

import lombok.extern.slf4j.Slf4j;
import org.mounanga.userservice.configuration.ApplicationProperties;
import org.mounanga.userservice.entity.Profile;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(RoleRepository roleRepository,
                                        UserRepository userRepository,
                                        PasswordEncoder passwordEncoder,
                                        ApplicationProperties applicationProperties) {
        return args -> {
            if (!roleRepository.existsBy()){
                log.info("*************************************************************");
                roleRepository.save(
                        Role.builder().name("USER").description("the default role that all users should have").build()
                );
                log.info("USER role added");
                roleRepository.save(
                        Role.builder().name("ADMIN").description("the default role that all administrators should have").build()
                );
                log.info("ADMIN role added");
                roleRepository.save(
                        Role.builder().name("SUPER_ADMIN").description("the default role that all super administrators should have").build()
                );
                log.info("SUPER admin role added");
            }
            if(!userRepository.existsBy()){
                log.info("************************************************************");
                String system = "SYSTEM";
                Profile profile = Profile.builder().gender(Gender.M).firstname(system).lastname(system).nationality(system).birthday(LocalDate.now())
                        .pin(system).placeOfBirth(system).createdDate(LocalDateTime.now()).createBy(system).build();
                User user = new User();
                user.setProfile(profile);
                user.setEnabled(Boolean.TRUE);
                user.setUsername(system);
                user.setEmail(applicationProperties.getSuperUserEmail());
                user.setPasswordNeedsToBeChanged(true);
                user.setLastLogin(LocalDateTime.now());
                String password = UUID.randomUUID().toString();
                user.setPassword(passwordEncoder.encode(password));
                User savedUser = userRepository.save(user);
                log.info("super user added with username {}", savedUser.getUsername());
                log.info("super user added with password {}", password);
                log.info("You must change this default password");
                List<Role> roles = roleRepository.findAll();
                user.setRoles(roles);
                userRepository.save(user);
                log.info("all roles added to super user");
                log.info("*************************************************************");
            }
        };
    }

}
