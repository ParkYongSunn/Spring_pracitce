package com.kdt.Config;

import com.kdt.Library.LibraryPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibraryConfig {

    @Bean
    public LibraryPolicy libraryPolicy(){
        return new LibraryPolicy("KDT 도서관", 20);
    }
}
