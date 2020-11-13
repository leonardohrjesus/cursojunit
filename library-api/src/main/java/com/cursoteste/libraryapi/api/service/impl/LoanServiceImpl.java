package com.cursoteste.libraryapi.api.service.impl;

import com.cursoteste.libraryapi.api.exception.BusinessException;
import com.cursoteste.libraryapi.api.model.entity.Loan;
import com.cursoteste.libraryapi.api.model.repository.LoanRepository;
import com.cursoteste.libraryapi.api.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

     private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository){
        this.repository= repository;
    }


    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookandNotReturned(loan.getBook())){
            throw  new BusinessException("Book already loaned");
        }

        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }
}
