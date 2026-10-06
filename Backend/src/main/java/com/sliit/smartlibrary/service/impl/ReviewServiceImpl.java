package com.sliit.smartlibrary.service.impl;
import com.sliit.smartlibrary.dto.ReviewRequest;
import com.sliit.smartlibrary.entity.*;
import com.sliit.smartlibrary.exception.*;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.service.ReviewService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;import java.util.*;
@Service
public class ReviewServiceImpl implements ReviewService {
 private final ReviewRepository reviews;private final UserRepository users;private final BookRepository books;
 public ReviewServiceImpl(ReviewRepository reviews,UserRepository users,BookRepository books){this.reviews=reviews;this.users=users;this.books=books;}
 public Review save(Long memberId,ReviewRequest r){if(r.getRating()==null||r.getRating()<1||r.getRating()>5)throw new BadRequestException("Rating must be between 1 and 5.");Review rev=reviews.findByMemberIdAndBookId(memberId,r.getBookId()).orElse(new Review());rev.setMember(users.findById(memberId).orElseThrow(()->new ResourceNotFoundException("Member not found.")));rev.setBook(books.findById(r.getBookId()).orElseThrow(()->new ResourceNotFoundException("Book not found.")));rev.setRating(r.getRating());rev.setComment(r.getComment());rev.setCreatedAt(LocalDateTime.now());return reviews.save(rev);}
 public List<Review> forBook(Long bookId){return reviews.findByBookIdOrderByCreatedAtDesc(bookId);}
 public void delete(Long id,Long memberId){Review r=reviews.findById(id).orElseThrow(()->new ResourceNotFoundException("Review not found."));if(!r.getMember().getId().equals(memberId))throw new BadRequestException("You can only remove your own review.");reviews.delete(r);}
}
