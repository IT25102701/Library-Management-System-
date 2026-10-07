package com.sliit.smartlibrary.service.impl;
import com.sliit.smartlibrary.dto.BookRequest;
import com.sliit.smartlibrary.entity.*;
import com.sliit.smartlibrary.exception.*;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.enums.ReservationStatus;
import com.sliit.smartlibrary.service.BookService;
import com.sliit.smartlibrary.validation.IsbnValidator;
import com.sliit.smartlibrary.validation.ValidationRules;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class BookServiceImpl implements BookService {
 private final BookRepository books; private final CategoryRepository categories; private final AuthorRepository authors; private final PublisherRepository publishers; private final BookCopyRepository copies; private final ReservationRepository reservations;
 public BookServiceImpl(BookRepository books,CategoryRepository categories,AuthorRepository authors,PublisherRepository publishers,BookCopyRepository copies,ReservationRepository reservations){this.books=books;this.categories=categories;this.authors=authors;this.publishers=publishers;this.copies=copies;this.reservations=reservations;}
 public List<Book> search(String q,Long categoryId){if(categoryId!=null)return books.findByCategoryId(categoryId);if(q!=null&&!q.isBlank())return books.search(q.trim());return books.findAll();}
 public Book get(Long id){return books.findById(id).orElseThrow(()->new ResourceNotFoundException("Book not found: "+id));}
 public Book create(BookRequest r){requireUniqueIsbn(r.getIsbn(),null);return books.save(apply(new Book(),r));}
 public Book update(Long id,BookRequest r){Book b=get(id);requireUniqueIsbn(r.getIsbn(),id);return books.save(apply(b,r));}
 public void delete(Long id){
  Book b=get(id);
  if(copies.existsByBookId(id))throw new BadRequestException("This book still has physical copies. Delete its copies in Assets & Inventory first.");
  if(reservations.existsByBookIdAndStatus(id,ReservationStatus.ACTIVE))throw new BadRequestException("This book has active reservations. Cancel or fulfil them before deleting the book.");
  books.delete(b);
 }
 /** Duplicate ISBN check that ignores hyphens and spaces ("978-0-..." equals "9780..."). */
 private void requireUniqueIsbn(String isbn,Long ownId){
  if(isbn==null||isbn.isBlank())return;
  String key=ValidationRules.normalizeIsbn(isbn);
  boolean clash=books.findAll().stream().anyMatch(x->!x.getId().equals(ownId)&&ValidationRules.normalizeIsbn(x.getIsbn()).equals(key));
  if(clash)throw new BadRequestException("Another book already has this ISBN.");
 }
 private Book apply(Book b,BookRequest r){
  if(r.getTitle()==null||r.getTitle().isBlank())throw new BadRequestException("Book title is required.");
  if(r.getIsbn()==null||r.getIsbn().isBlank())throw new BadRequestException("ISBN is required.");
  if(r.getCategoryId()==null||r.getAuthorId()==null)throw new BadRequestException("Category and author are required.");
  if(!IsbnValidator.isValidIsbn(r.getIsbn()))throw new BadRequestException("Enter a valid ISBN-10 or ISBN-13; the check digit does not match.");
  ValidationRules.requireYearInRange(r.getPublishedYear(),1450,"Published year");
  b.setTitle(r.getTitle().trim());b.setIsbn(r.getIsbn().trim());b.setCategory(categories.findById(r.getCategoryId()).orElseThrow(()->new BadRequestException("Select a valid category.")));b.setAuthor(authors.findById(r.getAuthorId()).orElseThrow(()->new BadRequestException("Select a valid author.")));
  b.setPublisher(r.getPublisherId()==null?null:publishers.findById(r.getPublisherId()).orElseThrow(()->new BadRequestException("Select a valid publisher.")));b.setDescription(ValidationRules.clean(r.getDescription()));b.setPublishedYear(r.getPublishedYear());b.setCoverUrl(ValidationRules.clean(r.getCoverUrl()));return b;
 }
}
