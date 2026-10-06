package com.sliit.smartlibrary.entity;

import com.sliit.smartlibrary.enums.BookCondition;
import com.sliit.smartlibrary.enums.CopyStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="inventory_audits")
public class InventoryAudit {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.EAGER, optional=false)
    @JoinColumn(name="book_copy_id", nullable=false)
    private BookCopy bookCopy;

    @Column(name="audited_at", nullable=false)
    private LocalDateTime auditedAt = LocalDateTime.now();

    @Column(name="checked_by", length=120)
    private String checkedBy;

    @Enumerated(EnumType.STRING)
    @Column(name="recorded_condition", length=20)
    private BookCondition condition;

    @Enumerated(EnumType.STRING)
    @Column(name="recorded_status", length=20)
    private CopyStatus status;

    @Column(name="shelf_location", length=80)
    private String shelfLocation;

    @Column(length=500)
    private String notes;

    public InventoryAudit(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public BookCopy getBookCopy(){return bookCopy;} public void setBookCopy(BookCopy bookCopy){this.bookCopy=bookCopy;}
    public LocalDateTime getAuditedAt(){return auditedAt;} public void setAuditedAt(LocalDateTime auditedAt){this.auditedAt=auditedAt;}
    public String getCheckedBy(){return checkedBy;} public void setCheckedBy(String checkedBy){this.checkedBy=checkedBy;}
    public BookCondition getCondition(){return condition;} public void setCondition(BookCondition condition){this.condition=condition;}
    public CopyStatus getStatus(){return status;} public void setStatus(CopyStatus status){this.status=status;}
    public String getShelfLocation(){return shelfLocation;} public void setShelfLocation(String shelfLocation){this.shelfLocation=shelfLocation;}
    public String getNotes(){return notes;} public void setNotes(String notes){this.notes=notes;}
}
