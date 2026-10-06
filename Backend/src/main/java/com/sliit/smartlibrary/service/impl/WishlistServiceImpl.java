package com.sliit.smartlibrary.service.impl;
import com.sliit.smartlibrary.entity.*;
import com.sliit.smartlibrary.exception.*;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.service.WishlistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlist;private final UserRepository users;private final BookRepository books;
    public WishlistServiceImpl(WishlistRepository wishlist,UserRepository users,BookRepository books){this.wishlist=wishlist;this.users=users;this.books=books;}
    public List<WishlistItem> forMember(Long id){return wishlist.findByMemberIdOrderByCreatedAtDesc(id);}
    public WishlistItem add(Long memberId,Long bookId){if(wishlist.existsByMemberIdAndBookId(memberId,bookId))throw new BadRequestException("This book is already in your wishlist.");WishlistItem w=new WishlistItem();w.setMember(users.findById(memberId).orElseThrow(()->new ResourceNotFoundException("Member not found.")));w.setBook(books.findById(bookId).orElseThrow(()->new ResourceNotFoundException("Book not found.")));w.setCreatedAt(LocalDateTime.now());return wishlist.save(w);}
    @Transactional public void remove(Long memberId,Long bookId){wishlist.deleteByMemberIdAndBookId(memberId,bookId);}
}
