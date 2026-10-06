package com.sliit.smartlibrary.service.impl;
import com.sliit.smartlibrary.dto.BookRequest;
import com.sliit.smartlibrary.entity.*;
import com.sliit.smartlibrary.exception.*;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.service.BookService;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class BookServiceImpl implements BookService {
 private final BookRepository books; private final CategoryRepository categories; private final AuthorRepository authors; private final PublisherRepository publishers;
 public BookServiceImpl(BookRepository books,CategoryRepository categories,AuthorRepository authors,PublisherRepository publishers){this.books=books;this.categories=categories;this.authors=authors;this.publishers=publishers;}
 public List<Book> search(String q,Long categoryId){if(categoryId!=null)return books.findByCategoryId(categoryId);if(q!=null&&!q.isBlank())return books.search(q.trim());return books.findAll();}
 public Book get(Long id){return books.findById(id).orElseThrow(()->new ResourceNotFoundException("Book not found: "+id));}
 public Book create(BookRequest r){if(r.getIsbn()!=null&&books.existsByIsbnIgnoreCase(r.getIsbn()))throw new BadRequestException("A book with this ISBN already exists.");return books.save(apply(new Book(),r));}
 public Book update(Long id,BookRequest r){Book b=get(id);if(r.getIsbn()!=null&&!r.getIsbn().equalsIgnoreCase(b.getIsbn())&&books.existsByIsbnIgnoreCase(r.getIsbn()))throw new BadRequestException("A book with this ISBN already exists.");return books.save(apply(b,r));}
 public void delete(Long id){books.delete(get(id));}
 private Book apply(Book b,BookRequest r){
  if(r.getTitle()==null||r.getTitle().isBlank())throw new BadRequestException("Book title is required.");
  if(r.getIsbn()==null||r.getIsbn().isBlank())throw new BadRequestException("ISBN is required.");
  if(r.getCategoryId()==null||r.getAuthorId()==null)throw new BadRequestException("Category and author are required.");
  b.setTitle(r.getTitle().trim());b.setIsbn(r.getIsbn().trim());b.setCategory(categories.findById(r.getCategoryId()).orElseThrow(()->new BadRequestException("Select a valid category.")));b.setAuthor(authors.findById(r.getAuthorId()).orElseThrow(()->new BadRequestException("Select a valid author.")));
  b.setPublisher(r.getPublisherId()==null?null:publishers.findById(r.getPublisherId()).orElseThrow(()->new BadRequestException("Select a valid publisher.")));b.setDescription(r.getDescription());b.setPublishedYear(r.getPublishedYear());b.setCoverUrl(r.getCoverUrl());return b;
 }
}
