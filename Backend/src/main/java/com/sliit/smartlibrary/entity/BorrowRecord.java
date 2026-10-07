// This file belongs to the package com.sliit.smartlibrary.entity (its folder in the project).
package com.sliit.smartlibrary.entity;

// Imports the BorrowStatus enum (a fixed list of values).
import com.sliit.smartlibrary.enums.BorrowStatus;
// Imports JPA annotations (@Entity, @Table, @Column, @Id, ...) that map this class to a database table.
import jakarta.persistence.*;
// Imports LocalDate, a date without a time (e.g. a due date).
import java.time.LocalDate;

// Marks this class as a JPA entity: each object is stored as one row in a database table.
@Entity
// Stores these objects in the database table named "borrow_records".
@Table(name = "borrow_records")
// Entity class BorrowRecord: each BorrowRecord object is one row in the "borrow_records" table.
public class BorrowRecord {

    // Marks the next field as the primary key (the unique id of each row).
    @Id
    // The database generates the id automatically (auto-increment).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Field "id" (Long): the unique id (primary key).
    private Long id;

    // Many rows here can point to one row of the related table (a foreign-key link); EAGER = the linked row is loaded together with this one; optional = false = the link is required.
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    // The link is stored in the database column "member_id".
    @JoinColumn(name = "member_id")
    // Field "member" (User): the library member this record belongs to.
    private User member;

    // Many rows here can point to one row of the related table (a foreign-key link); EAGER = the linked row is loaded together with this one; optional = false = the link is required.
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    // The link is stored in the database column "book_copy_id".
    @JoinColumn(name = "book_copy_id")
    // Field "bookCopy" (BookCopy): the physical copy involved.
    private BookCopy bookCopy;

    // Many rows here can point to one row of the related table (a foreign-key link); EAGER = the linked row is loaded together with this one; optional = false = the link is required.
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    // The link is stored in the database column "issued_by".
    @JoinColumn(name = "issued_by")
    // Field "issuedBy" (User): the staff user who issued the book.
    private User issuedBy;

    // Database column settings: required (cannot be empty in the database).
    @Column(nullable = false)
    // Field "issueDate" (LocalDate): the date the book was issued.
    private LocalDate issueDate;

    // Database column settings: required (cannot be empty in the database).
    @Column(nullable = false)
    // Field "dueDate" (LocalDate): the date the book must be returned by.
    private LocalDate dueDate;

    // Field "returnDate" (LocalDate): the date the book was returned (empty while on loan).
    private LocalDate returnDate;

    // Saves the enum value as text (e.g. "ACTIVE") instead of a number.
    @Enumerated(EnumType.STRING)
    // Database column settings: required (cannot be empty in the database), holds at most 20 characters.
    @Column(nullable = false, length = 20)
    // Field "status" (BorrowStatus): the status of this borrow record; starts as BorrowStatus.ISSUED.
    private BorrowStatus status = BorrowStatus.ISSUED;

    // Database column settings: required (cannot be empty in the database).
    @Column(nullable = false)
    // Field "renewalCount" (Integer): how many times the loan has been renewed; starts as 0.
    private Integer renewalCount = 0;

    // Empty constructor: lets JPA and the JSON converter create an empty object.
    public BorrowRecord() {}

    // Getter: returns the unique id (primary key).
    public Long getId() {
        // Gives back the value of the id field.
        return id;
    } // end of getId()

    // Setter: changes the unique id (primary key) to the given value.
    public void setId(Long id) {
        // Stores the given id in this object's field.
        this.id = id;
    } // end of setId()

    // Getter: returns the library member this record belongs to.
    public User getMember() {
        // Gives back the value of the member field.
        return member;
    } // end of getMember()

    // Setter: changes the library member this record belongs to to the given value.
    public void setMember(User member) {
        // Stores the given member in this object's field.
        this.member = member;
    } // end of setMember()

    // Getter: returns the physical copy involved.
    public BookCopy getBookCopy() {
        // Gives back the value of the book copy field.
        return bookCopy;
    } // end of getBookCopy()

    // Setter: changes the physical copy involved to the given value.
    public void setBookCopy(BookCopy bookCopy) {
        // Stores the given book copy in this object's field.
        this.bookCopy = bookCopy;
    } // end of setBookCopy()

    // Getter: returns the staff user who issued the book.
    public User getIssuedBy() {
        // Gives back the value of the issued by field.
        return issuedBy;
    } // end of getIssuedBy()

    // Setter: changes the staff user who issued the book to the given value.
    public void setIssuedBy(User issuedBy) {
        // Stores the given issued by in this object's field.
        this.issuedBy = issuedBy;
    } // end of setIssuedBy()

    // Getter: returns the date the book was issued.
    public LocalDate getIssueDate() {
        // Gives back the value of the issue date field.
        return issueDate;
    } // end of getIssueDate()

    // Setter: changes the date the book was issued to the given value.
    public void setIssueDate(LocalDate issueDate) {
        // Stores the given issue date in this object's field.
        this.issueDate = issueDate;
    } // end of setIssueDate()

    // Getter: returns the date the book must be returned by.
    public LocalDate getDueDate() {
        // Gives back the value of the due date field.
        return dueDate;
    } // end of getDueDate()

    // Setter: changes the date the book must be returned by to the given value.
    public void setDueDate(LocalDate dueDate) {
        // Stores the given due date in this object's field.
        this.dueDate = dueDate;
    } // end of setDueDate()

    // Getter: returns the date the book was returned (empty while on loan).
    public LocalDate getReturnDate() {
        // Gives back the value of the return date field.
        return returnDate;
    } // end of getReturnDate()

    // Setter: changes the date the book was returned (empty while on loan) to the given value.
    public void setReturnDate(LocalDate returnDate) {
        // Stores the given return date in this object's field.
        this.returnDate = returnDate;
    } // end of setReturnDate()

    // Getter: returns the status.
    public BorrowStatus getStatus() {
        // Gives back the value of the status field.
        return status;
    } // end of getStatus()

    // Setter: changes the status to the given value.
    public void setStatus(BorrowStatus status) {
        // Stores the given status in this object's field.
        this.status = status;
    } // end of setStatus()

    // Getter: returns how many times the loan has been renewed.
    public Integer getRenewalCount() {
        // Gives back the value of the renewal count field.
        return renewalCount;
    } // end of getRenewalCount()

    // Setter: changes how many times the loan has been renewed to the given value.
    public void setRenewalCount(Integer renewalCount) {
        // Stores the given renewal count in this object's field.
        this.renewalCount = renewalCount;
    } // end of setRenewalCount()
} // end of class BorrowRecord
