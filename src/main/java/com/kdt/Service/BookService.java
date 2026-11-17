package com.kdt.Service;

import com.kdt.Entity.Author;
import com.kdt.Entity.Book;
import com.kdt.Library.LibraryPolicy;
import com.kdt.Repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final LibraryPolicy libraryPolicy;

    public Book save(Book book){
        return this.bookRepository.save(book);
    }

    public List<Book> findAll(){
        return this.bookRepository.findAll();
    }

    public Book findById(Long id){
        return this.bookRepository.findById(id).orElse(null);
    }

    public List<Book> findByAuthorId(Long authorId){
        return this.bookRepository.findByAuthorId(authorId);
    }

    public long count(){
        return this.bookRepository.count();
    }

    public boolean existsByTitleAndAuthorId(String title, Long authorId){
        return this.bookRepository.existsByTitleAndAuthorId(title, authorId);
    }

    @Transactional
    public Book register(String title, Integer price, String authorName) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("책 제목은 비어 있을 수 없다");
        }

        if (title.length() > this.libraryPolicy.getMaxTitleLength()) {
            throw new IllegalArgumentException("제목은 " + this.libraryPolicy.getMaxTitleLength() + "자 이하여야 한다. = " + title);
        }

        if (price == null || price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 한다.");
        }

        Author author = this.authorService.findByName(authorName);
        if (author == null) {
            throw new IllegalArgumentException("저자가 없다. = " + authorName);
        }

        if (this.bookRepository.existsByTitleAndAuthorId(title, author.getId())) {
            throw new IllegalArgumentException("이미 등록된 책입니다. = " + title);
        }

        Book book = new Book(title, price);
        author.addBook(book);
        return this.bookRepository.save(book);
    }

    @Transactional
    public Book changePrice(Long id, Integer newPrice){
        Book book = this.findById(id);
        if (book == null) {
            throw new IllegalArgumentException("책이 없습니다. = " + id);
        }

        if (newPrice == null || newPrice < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 한다.");
        }

        book.changePrice(newPrice);
        return this.bookRepository.save(book);
    }

    @Transactional
    public Book delete(Long id){
        Book book = this.findById(id);
        if (book == null) {
            throw new IllegalArgumentException("책이 없습니다. = " + id);
        }

        this.bookRepository.delete(book);
        return book;
    }
}
