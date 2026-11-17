package com.kdt.Controller;

import com.kdt.Entity.Author;
import com.kdt.Entity.Book;
import com.kdt.Service.AuthorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    private String authorBooks(String name){
        Author author = this.authorService.findByName(name);

        if(author == null){
            return "missing";
        }

        List<Book> books = author.getBooks();

        if(books.isEmpty()){
            return "none/ncount = 0";
        }

        StringBuilder lines = new StringBuilder();
        for(Book book : books){
            lines.append(book.getTitle()).append("\n");
        }
        lines.append("count = ").append(books.size());
        return lines.toString();
    }

    @GetMapping("/library/author")
    public String authors(){
        List<Author> authors = this.authorService.findAll();

        if(authors.isEmpty()){
            return "none";
        }

        StringBuilder lines = new StringBuilder();

        for(int i = 0; i < authors.size(); i++){
            Author author = authors.get(i);

            if( i > 0){
                lines.append("\n");
            }

            lines.append(author.getId()).append(" ").append(author.getName()).append(" ( ").append(author.getCountry()).append(") books =")
                    .append(author.getBooks().size());
        }
        return lines.toString();
    }

    @GetMapping("/library/authors/sample")
    public String same(){
        Long id = 1L;

        Author author = this.authorService.findById(id);

        if(author == null){
            return "missing";
        }

        return author.getId() + " " + author.getName() + " (" +author.getCountry() + ") ";
    }

    @GetMapping("/library/authors/hangang/books")
    public String hangangBooks(){return authorBooks("한강");}

    @GetMapping("/library/authors/newbie/books")
    public String newbieBooks() {return authorBooks("박신인");}

    @GetMapping("/library/authors/ghost/books")
    public String ghostBooks() {return authorBooks("없는 작가");}
}
