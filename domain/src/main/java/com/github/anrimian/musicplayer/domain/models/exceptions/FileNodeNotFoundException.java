package com.github.anrimian.musicplayer.domain.models.exceptions;

/**
 * Created on 26.10.2017.
 */

public class FileNodeNotFoundException extends RuntimeException {
    public FileNodeNotFoundException() {
    }

    public FileNodeNotFoundException(String s) {
        super(s);
    }
}
