package org.mounanga.userservice.repository;

import org.mounanga.userservice.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);

    @Query("select r from Role r where r.name like :kw or r.description like :kw")
    Page<Role> findByNameOrDescription(@Param("kw") String keyword, Pageable pageable);

    boolean existsBy();
}
