package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface BookRepository extends JpaRepository<Book,Long>{
    boolean existsByIsbnIgnoreCase(String isbn);
    @Query("select b from Book b where lower(b.title) like lower(concat('%',:q,'%')) or lower(b.isbn) like lower(concat('%',:q,'%')) or lower(b.author.name) like lower(concat('%',:q,'%')) or lower(b.category.name) like lower(concat('%',:q,'%'))")
    List<Book> search(@Param("q") String q);
    List<Book> findByCategoryId(Long categoryId);
}
