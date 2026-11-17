package com.kdt.Controller;

import com.kdt.Library.LibraryPolicy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryInfoController {
    private final LibraryPolicy first;
    private final LibraryPolicy second;

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
}
