package com.kdt.Repository;

import com.kdt.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book,Long> {
    List<Book> findByAuthorId(Long authorId);
    List<Book> findByTitle(String title);

    long countByAuthorId(Long authorId);
    boolean existsByTitleAndAuthorId(String title, Long authorId);
}
