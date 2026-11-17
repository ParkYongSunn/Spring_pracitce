package com.kdt.Entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Table(name = "lib_author")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String country;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();

    public Author(String name, String country){
        this.name = name;
        this.country = country;
    }

    protected Author(){

    }



    public List<Book> getBooks(){
        return Collections.unmodifiableList(books);
    }



    public void addBook(Book book){
        if(book == null){
            throw new IllegalArgumentException("책은 null일 수 없다.");
        }

        if(book.getAuthor() != null && book.getAuthor() != this){
            throw new IllegalArgumentException("다른 저자의 책을 옮기지 않는다.");
        }

        if(!this.books.contains(book)){
            this.books.add(book);
        }

        book.setAuthor(this);
    }

    public void removeBook(Book book){
        if(book == null){
            throw new IllegalArgumentException("책은 null일 수 없다.");
        }

        if(this.books.remove(book)){
            book.setAuthor(null);
        }
    }
}
