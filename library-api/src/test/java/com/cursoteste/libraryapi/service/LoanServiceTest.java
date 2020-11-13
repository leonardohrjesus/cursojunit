package com.cursoteste.libraryapi.service;

import com.cursoteste.libraryapi.api.exception.BusinessException;
import com.cursoteste.libraryapi.api.model.entity.Book;
import com.cursoteste.libraryapi.api.model.entity.Loan;
import com.cursoteste.libraryapi.api.model.repository.LoanRepository;
import com.cursoteste.libraryapi.api.service.LoanService;
import com.cursoteste.libraryapi.api.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("Test")
public class LoanServiceTest {


    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest(){

        //cenario
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan =Loan.builder()
                .id(1l)
                .loanDate(LocalDate.now())
                .customer(customer)
                .book(book).build();

        when(repository.existsByBookandNotReturned(book)).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan) ;

        //execucao
        Loan loan = service.save(savingLoan);

        //verificacao
        assertThat(loan.getId() ).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId() ).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate() ).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao salvar  empréstimo com livro já emprestado")
    public void loanedBookSaveTest(){

        //cenario
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();


        when(repository.existsByBookandNotReturned(book)).thenReturn(true);

        //execucao
        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");
        verify(repository,never()).save(savingLoan);
    }



    @Test
    @DisplayName(" Deve obter as informações de um emprestimo pelo ID")
    public void getLoanDetaisTest(){
        //cenario
        Long id = 1l;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //execução
        Optional<Loan> result = service.getById(id);

        //verificação
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer() ).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook() ).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate() ).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);

    }

    @Test
    @DisplayName("Deve atualizar um emprestimo")
    public  void updateLoanTest(){
        Loan loan = createLoan();
        loan.setId(1l);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);



        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }

    public Loan createLoan(){
        Book book = Book.builder().id(1l).build();
        String customer = "Fulano";

        return Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();


    }

}
