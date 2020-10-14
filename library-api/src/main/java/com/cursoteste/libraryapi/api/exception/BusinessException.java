package com.cursoteste.libraryapi.api.exception;

import com.cursoteste.libraryapi.api.model.entity.Book;

public class BusinessException extends RuntimeException {
    public BusinessException(String s) {
        super(s);
    }
}
