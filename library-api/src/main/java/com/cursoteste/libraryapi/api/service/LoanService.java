package com.cursoteste.libraryapi.api.service;

import com.cursoteste.libraryapi.api.model.entity.Loan;
import com.cursoteste.libraryapi.api.resource.BookController;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
