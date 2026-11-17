package com.kdt.Controller;

import com.kdt.Entity.Author;
import com.kdt.Entity.Book;
import com.kdt.Service.AuthorService;
import com.kdt.Service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final AuthorService authorService;

    @GetMapping("/library/books")
    public String boos() {
        List<Book> books = this.bookService.findAll();

        if (boos().isEmpty()) {
            return "none";
        }

        StringBuilder lines = new StringBuilder();

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);

            if (i > 0) {
                lines.append("\n");
            }

            lines.append(book.getId()).append("\n")
                    .append(book.getTitle()).append("\n")
                    .append(book.getPrice()).append("\n")
                    .append(book.getAuthor()).append("\n")
                    .append(book.getCreatedAt()).append("\n")
                    .append(book.getUpdatedAt());
        }
        return lines.toString();
    }

    @GetMapping("/library/books/sample")
    public String sample() {
        Long id = 1L;

        Book book = this.bookService.findById(id);
        if (book == null) {
            return "missing";
        }

        return book.getTitle() + " by " + book.getAuthor().getName() + " ( " + book.getPrice() + "원 )";
    }

    @GetMapping("/library/books/by-hangang")
    public String byHangang() {
        Author author = this.authorService.findByName("한강");
        if (author == null) {
            return "missing";
        }

        List<Book> books = this.bookService.findByAuthorId(author.getId());
        if (books.isEmpty()) {
            return "none/0none = 0";
        }

        StringBuilder lines = new StringBuilder();

        for (Book book : books) {
            lines.append(book.getId()).append(" ").append(book.getTitle()).append("\n");
        }

        lines.append("count = ").append(books.size());
        return lines.toString();
    }

    @GetMapping("/library/books/load-check")
    public String loadCheck() {
        Long id = 1L;
        System.out.println("load : before book lookup");

        Book book = this.bookService.findById(id);

        if (book == null) {
            return "missing";
        }

        System.out.println("load : book loaded, title =" + book.getTitle());

        Author author = book.getAuthor();

        System.out.println("load : author id = " + book.getId());
        System.out.println("load : before author name");

        String name = author.getName();

        System.out.println("load : after author name = " + name);

        return book.getTitle() + " by " + name;
    }

    @GetMapping("library/books/register")
    public String register() {
        String title = "흰";
        Integer price = 12000;
        String authorName = "한강";

        try {
            Book saved = this.bookService.register(title, price, authorName);
            StringBuilder lines = new StringBuilder();
            lines.append("REGISTERED : id = ").append(saved.getId()).append("title = ").append(saved.getTitle()).append("author = ").append(saved.getAuthor().getName())
                    .append("createdAt = ").append(saved.getCreatedAt());

            return lines.toString();
        } catch (IllegalArgumentException e) {
            return "ERROR : " + e.getMessage();
        }

    }

    @GetMapping("/library/books/price-up")
    public String priceUp(){
        Long id = 1L;
        int plus = 1000;

        try{
            Book book = this.bookService.findById(id);
            if(book == null){
                return "ERROR: 책이 없습니다 : " + id;

            }
            Integer beforePrice = book.getPrice();
            Instant beforeCreatedAt = book.getCreatedAt();
            Instant beforeUpdatedAt = book.getUpdatedAt();

            Book saved = this.bookService.changePrice(id, beforePrice + plus);

            StringBuilder lines = new StringBuilder();
            lines.append("book = ").append(saved.getId()).append(" ")
                    .append(saved.getTitle()).append("\n");

            lines.append("price = ").append(beforePrice).append(" -> ").append(saved.getPrice()).append("\n");

            lines.append("createdAt kept = ").append(Objects.equals(beforeCreatedAt, saved.getCreatedAt())).append("\n");

            lines.append("updatedAt = ").append(beforeUpdatedAt).append(" -> ").append(saved.getUpdatedAt());

            return lines.toString();

        } catch (IllegalArgumentException e){
            return "ERROR : " + e.getMessage();
        }
    }
    @GetMapping("/library/books/delete")
    public String deleteBook(){
        Long id = 1L;

        try{
            Book deleted = this.bookService.delete(id);
            return "DELETED : id = " + deleted.getId() + " title =" + deleted.getTitle();
        } catch (IllegalArgumentException e){
            return "ERROR : " + e.getMessage();
        }
    }
}
