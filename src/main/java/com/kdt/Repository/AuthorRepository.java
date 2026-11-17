package com.kdt.Repository;

import com.kdt.Entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String name);
    boolean existsByName(String name);
}
