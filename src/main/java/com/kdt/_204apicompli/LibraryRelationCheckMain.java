package com.kdt._204apicompli;


import com.kdt.Entity.Author;
import com.kdt.Entity.Book;

public class LibraryRelationCheckMain {
    public static void main(String[] args) {

        Author hk = new Author("한강", "KR");
        Author ky = new Author("김영하", "KR");

        Book b1 = new Book("휜", 12000);

        hk.addBook(b1);
        hk.addBook(b1);

        System.out.println("count =" + hk.getBooks().size());
        System.out.println("author =" + b1.getAuthor().getName());

        try{
            ky.addBook(b1);
        } catch (IllegalArgumentException e){
            System.out.println("move blocked");
        }
        System.out.println("ky count = " + ky.getBooks().size());

        hk.removeBook(b1);
        System.out.println("atfer remove =" + ky.getBooks().size());
        System.out.println("author cleared = " + (b1.getAuthor() == null));
    }
}
