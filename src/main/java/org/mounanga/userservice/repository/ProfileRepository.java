package org.mounanga.userservice.repository;

import org.mounanga.userservice.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    Optional<Profile> findByPin(String pin);
    boolean existsByPin(String pin);
}
