package com.sliit.smartlibrary.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="wishlist_items",uniqueConstraints=@UniqueConstraint(columnNames={"member_id","book_id"}))
public class WishlistItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="member_id") private User member;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="book_id") private Book book;
    @Column(nullable=false) private LocalDateTime createdAt;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public User getMember(){return member;} public void setMember(User member){this.member=member;}
    public Book getBook(){return book;} public void setBook(Book book){this.book=book;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}
}
