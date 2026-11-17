package com.kdt.Library;

import lombok.Getter;

@Getter
public class LibraryPolicy {
    private final String libraryName;
    private final int maxTitleLength;

    public LibraryPolicy(String libraryName, int maxTitleLength){
        this.libraryName = libraryName;
        this.maxTitleLength = maxTitleLength;
    }
}
