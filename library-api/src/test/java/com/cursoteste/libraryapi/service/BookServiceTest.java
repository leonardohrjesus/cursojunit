package com.cursoteste.libraryapi.service;


import com.cursoteste.libraryapi.api.exception.BusinessException;
import com.cursoteste.libraryapi.api.model.entity.Book;
import com.cursoteste.libraryapi.api.model.repository.BookRepository;
import com.cursoteste.libraryapi.api.service.BookService;
import com.cursoteste.libraryapi.api.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public  void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public  void  saveBookTest(){
        //cenario
        Book book = createValidBook();
        Mockito.when(repository.save(book)).thenReturn
                (Book.builder()
                        .id(1l)
                        .isbn("123")
                        .author("Fulano")
                        .title("As aventuras").build());

        //execução
        Book savedBook = service.save(book);
        
        
        
        //verificação
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");

    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public  void shouldNotSaveWithDuplicatedISBN(){
        //cenario
        Book book = createValidBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execução
         Throwable  excption = Assertions.catchThrowable(() -> service.save(book));

        //Verificacaoes
         assertThat(excption)
                 .isInstanceOf(BusinessException.class)
                 .hasMessage("Isbn já cadastrado.");

         Mockito.verify(repository, Mockito.never()).save(book);


    }

    @Test
    @DisplayName("Deve retornar um livro por Id")
    public void  getByIdTest(){
        //cenario
        Long id  = 1l;
        Book book= createValidBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacao
        assertThat( foundBook.isPresent()).isTrue();
        assertThat( foundBook.get().getId()).isEqualTo(id);
        assertThat( foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat( foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat( foundBook.get().getTitle()).isEqualTo(book.getTitle());

    }


    @Test
    @DisplayName("Deve retornar vazio ao obter  um livro por Id quando ele não existir na base")
    public void  bookNotFoundByIdTest(){
        //cenario
        Long id  = 1l;


        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Book> foundBook = service.getById(id);

        //verificacao
        assertThat( foundBook.isPresent()).isFalse();


    }

    @Test
    @DisplayName("Deve deletar  um livro  na base")
    public void  bookDeleteTest(){
        //cenario
        Long id  = 1l;
        Book book= createValidBook();
        book.setId(id);

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(()-> service.delete(book) );


        //verificacao
        Mockito.verify(repository, Mockito.times(1)).delete(book);

    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar  deletar  um livro  inexistente")
    public void  deleteInvalidBookTest(){
        //cenario
        Book book= new Book();


        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book) );


        //verificacao
        Mockito.verify(repository, Mockito.never()).delete(book);

    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar  atualizar   um livro  inexistente")
    public void  updateInvalidBookTest(){
        //cenario
        Book book= new Book();


        //execucao
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book) );


        //verificacao
        Mockito.verify(repository, Mockito.never()).save(book);

    }


    @Test
    @DisplayName("Deve   atualizar   um livro  ")
    public void  updateBookTest(){
        //cenario
        long id = 1l;

        //livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        //simulação
        Book updatedBook = Book.builder().id(id).build();
        updatedBook.setId(id);
        Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);


        //execucao
        Book book = service.update(updatingBook);

        //verificacao
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());



    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades ")
    public void  findBookTest(){
        //cenario
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0,10);

        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista,pageRequest,1);
        Mockito.when(repository.findAll(Mockito.any(Example.class),Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execução
        Page<Book> result = service.find(book,pageRequest);

        //verificações
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }



}
