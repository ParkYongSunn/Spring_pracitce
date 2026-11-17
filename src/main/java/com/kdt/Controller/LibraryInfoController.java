package com.kdt.Controller;

import com.kdt.Entity.Book;
import com.kdt.Library.LibraryPolicy;
import com.kdt.Service.AuthorService;
import com.kdt.Service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryInfoController {
    private final LibraryPolicy first;
    private final LibraryPolicy second;
    private AuthorService authorService;
    private BookService bookService;

    public LibraryInfoController(LibraryPolicy first, LibraryPolicy second) {
        this.first = first;
        this.second = second;
    }

    @GetMapping("/library/hello")
    public String hello(){
        return this.first.getLibraryName() + "max-title =" + this.first.getMaxTitleLength();
    }

    @GetMapping("/library/policy/same")
    public String same(){
        LibraryPolicy made = new LibraryPolicy(this.first.getLibraryName(), this.first.getMaxTitleLength());

        StringBuilder lines = new StringBuilder();

        lines.append("injected same = ").append(this.first == this.second).append("\n");
        return lines.toString();
    }

    @GetMapping("/library/report")
    public String report() {
        StringBuilder lines = new StringBuilder();
        lines.append("library=").append(this.first.getLibraryName()).append("\n");
        lines.append("authors=").append(this.authorService.count()).append("\n");
        lines.append("books=").append(this.bookService.count()).append("\n");
        Book top = null;
        for (Book book : this.bookService.findAll()) {
            if (top == null || book.getPrice() > top.getPrice()) {
                top = book;
            }
        }
        lines.append("most-expensive=");
        if (top == null) {
            lines.append("none");

        } else {
            lines.append(top.getTitle()).append(" ")
                    .append(top.getPrice()).append(" by ")
                    .append(top.getAuthor().getName());
        }
        return lines.toString();
    }
}


