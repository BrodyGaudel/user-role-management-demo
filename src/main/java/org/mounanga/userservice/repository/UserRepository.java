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
    boolean existsByNip(String nip);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("select u from User u where u.firstname like :kw or u.lastname like :kw or u.nip like :kw or u.email like :kw")
    Page<User> search(@Param("kw") String keyword, Pageable pageable);

    boolean existsBy();

}
