package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.dto.BookRequest;
import com.sliit.smartlibrary.entity.Book;
import java.util.*;
public interface BookService { List<Book> search(String q,Long categoryId); Book get(Long id); Book create(BookRequest r); Book update(Long id,BookRequest r); void delete(Long id); }
