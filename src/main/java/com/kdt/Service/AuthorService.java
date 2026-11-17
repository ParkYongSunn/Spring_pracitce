package com.kdt.Service;

import com.kdt.Entity.Author;
import com.kdt.Repository.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author save(Author author){
        return this.authorRepository.save(author);
    }

    public List<Author> findAll(){
        return this.authorRepository.findAll();
    }

    public Author findById(Long id){
        return this.authorRepository.findById(id).orElse(null);
    }

    public Author findByName(String name){
        return this.authorRepository.findByName(name).orElse(null);
    }

    public long count(){
        return this.authorRepository.count();
    }
}
