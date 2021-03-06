package com.cursoteste.libraryapi.model.repository;

import com.cursoteste.libraryapi.api.model.entity.Book;
import com.cursoteste.libraryapi.api.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com isbn informado")
    public void returnTrueWhenIsbnExists(){
        //cenario
        String isbn = "123";

        Book book =createNewBook(isbn);
        entityManager.persist(book);

        //execucao
        boolean exists = repository.existsByIsbn(isbn);

        //verificacao
        assertThat(exists).isTrue();
    }


    @Test
    @DisplayName("Deve retornar falso  quando existir o livro não existir  na base com isbn informado")
    public void returnFalseWhenIsbnExists(){
        //cenario
        String isbn = "123";
        
        //execucao
        boolean exists = repository.existsByIsbn(isbn);

        //verificacao
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por id.")
    public void findByIdTest(){
        //cenario
        Book book = createNewBook("123");
        entityManager.persist(book);

        //execucao
        Optional<Book> foundBook  = repository.findById(book.getId());

        //verificacao
        assertThat(foundBook.isPresent()).isTrue();

    }
    @Test
    @DisplayName("Deve salvar  um livro")
    public void saveBookTest(){
        //cenario
        Book book = createNewBook("123");

        //execucao
        Book savedBook = repository.save(book);

        //verificacao
        assertThat(savedBook.getId()).isNotNull();

    }

    @Test
    @DisplayName("Deve salvar  um livro")
    public void deleteBookTest(){
        //cenario
        Book book = createNewBook("123");
        entityManager.persist(book);
        Book foundBook = entityManager.find(Book.class,book.getId());

        //execucao
         repository.delete(foundBook );

        Book deleteBook = entityManager.find(Book.class,book.getId());

        //verificacao
        assertThat(deleteBook).isNull();

    }

    public static   Book createNewBook(String isbn){
        return  Book.builder().title("Aventuras").author("Fulano").isbn(isbn).build();
    }



}
