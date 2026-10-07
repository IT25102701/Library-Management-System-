package com.sliit.smartlibrary.entity;
import com.sliit.smartlibrary.enums.BorrowStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="borrow_records")
public class BorrowRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="member_id") private User member;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="book_copy_id") private BookCopy bookCopy;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="issued_by") private User issuedBy;
    @Column(nullable=false) private LocalDate issueDate;
    @Column(nullable=false) private LocalDate dueDate;
    private LocalDate returnDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private BorrowStatus status=BorrowStatus.ISSUED;
    @Column(nullable=false) private Integer renewalCount=0;
    public BorrowRecord(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public User getMember(){return member;} public void setMember(User member){this.member=member;}
    public BookCopy getBookCopy(){return bookCopy;} public void setBookCopy(BookCopy bookCopy){this.bookCopy=bookCopy;}
    public User getIssuedBy(){return issuedBy;} public void setIssuedBy(User issuedBy){this.issuedBy=issuedBy;}
    public LocalDate getIssueDate(){return issueDate;} public void setIssueDate(LocalDate issueDate){this.issueDate=issueDate;}
    public LocalDate getDueDate(){return dueDate;} public void setDueDate(LocalDate dueDate){this.dueDate=dueDate;}
    public LocalDate getReturnDate(){return returnDate;} public void setReturnDate(LocalDate returnDate){this.returnDate=returnDate;}
    public BorrowStatus getStatus(){return status;} public void setStatus(BorrowStatus status){this.status=status;}
    public Integer getRenewalCount(){return renewalCount;} public void setRenewalCount(Integer renewalCount){this.renewalCount=renewalCount;}
}
