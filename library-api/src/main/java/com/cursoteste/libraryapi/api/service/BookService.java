package com.cursoteste.libraryapi.api.service;

import com.cursoteste.libraryapi.api.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);
}
