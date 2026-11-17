package com.kdt.Runner;

import com.kdt.Library.LibraryPolicy;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class LibraryReadyRunner implements ApplicationRunner {
    private final LibraryPolicy libraryPolicy;

    public LibraryReadyRunner(LibraryPolicy libraryPolicy) {
        this.libraryPolicy = libraryPolicy;
    }

    @Override
    public void run(ApplicationArguments args){
        System.out.println("library ready : " + this.libraryPolicy.getLibraryName() + " max-title = " + this.libraryPolicy.getMaxTitleLength());
    }
}
