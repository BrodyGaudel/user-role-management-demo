package org.mounanga.userservice.repository;

import org.mounanga.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsBy();

    @Query("select u from User u where u.profile.firstname like :kw or u.profile.lastname like :kw or u.profile.pin like :kw")
    Page<User> search(@Param("kw") String keyword, Pageable pageable);
}
