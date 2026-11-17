package com.kdt.Runner;

import com.kdt.Entity.Author;
import com.kdt.Entity.Book;
import com.kdt.Service.AuthorService;
import com.kdt.Service.BookService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LibrarySeedRunner implements ApplicationRunner {
    private final AuthorService authorService;
    private final BookService bookService;

    public LibrarySeedRunner(AuthorService authorService, BookService bookService){
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @Override
    public void run(ApplicationArguments args){
        Author hangang = seedAuthor("한강", "KR");
        seedBook(hangang, "소년이 온다", 150000 );
        seedBook(hangang, "채식주의자", 13000);

        Author kim = seedAuthor("김영하", "KR");
        seedBook(kim, "살인자의 기억법", 12000);

        seedAuthorByCascade();

        seedAuthor("박신인", "KR");
    }

    private Author seedAuthor(String name, String country){
        Author existing = this.authorService.findByName(name);

        if(existing != null){
            System.out.println("seed skip author :" + name);
            return existing;
        }

        Author author = new Author(name, country);
        System.out.println("author id before save = " + author.getId());

        Author saved = this.authorService.save(author);
        System.out.println("seed author = " + name + "id = " + saved.getId());
        return saved;
    }

    private void seedBook(Author author, String title, int price){
        if(this.bookService.existsByTitleAndAuthorId(title, author.getId())){
            System.out.println("seed skip book :" + title);
            return;
        }

        Book book = new Book(title, price);
        book.setAuthor(author);

        Book saved = this.bookService.save(book);
        System.out.println("seed book = " + title + "id = " + saved.getId() + "createdAt = " + saved.getCreatedAt());
    }

    public void seedAuthorByCascade(){
        if(this.authorService.findByName("정유정") != null){
            System.out.println("seed skip cascade 정유정");
            return;
        }

        Author author = new Author("정유정", "KR");
        author.addBook(new Book("종의 기원", 14000));
        author.addBook(new Book("7년의 밤", 14500));
        Author saved = this.authorService.save(author);

        System.out.println("seed cascade 정유성 books = " + saved.getBooks().size());
    }

}
