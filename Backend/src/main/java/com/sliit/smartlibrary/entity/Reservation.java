package com.sliit.smartlibrary.entity;
import com.sliit.smartlibrary.enums.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="reservations")
public class Reservation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="member_id") private User member;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="book_id") private Book book;
    @Column(nullable=false) private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private ReservationStatus status=ReservationStatus.ACTIVE;
    public Reservation(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public User getMember(){return member;} public void setMember(User member){this.member=member;}
    public Book getBook(){return book;} public void setBook(Book book){this.book=book;}
    public LocalDateTime getReservedAt(){return reservedAt;} public void setReservedAt(LocalDateTime reservedAt){this.reservedAt=reservedAt;}
    public LocalDateTime getExpiresAt(){return expiresAt;} public void setExpiresAt(LocalDateTime expiresAt){this.expiresAt=expiresAt;}
    public ReservationStatus getStatus(){return status;} public void setStatus(ReservationStatus status){this.status=status;}
}
