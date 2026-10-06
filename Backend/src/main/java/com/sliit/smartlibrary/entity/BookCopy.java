package com.sliit.smartlibrary.entity;

import com.sliit.smartlibrary.enums.BookCondition;
import com.sliit.smartlibrary.enums.CopyStatus;
import jakarta.persistence.*;

@Entity @Table(name="book_copies")
public class BookCopy {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="book_id") private Book book;
    @Column(nullable=false,unique=true,length=80) private String barcode;
    @Column(name="qr_code",length=100) private String qrCode;
    @Column(nullable=false,length=80) private String shelfLocation;
    @Enumerated(EnumType.STRING) @Column(name="book_condition",nullable=false,length=20) private BookCondition condition=BookCondition.GOOD;
    @Enumerated(EnumType.STRING) @Column(name="copy_status",nullable=false,length=20) private CopyStatus status=CopyStatus.AVAILABLE;
    private String auditNote;

    public BookCopy(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public Book getBook(){return book;} public void setBook(Book book){this.book=book;}
    public String getBarcode(){return barcode;} public void setBarcode(String barcode){this.barcode=barcode;}
    public String getQrCode(){return qrCode;} public void setQrCode(String qrCode){this.qrCode=qrCode;}
    public String getShelfLocation(){return shelfLocation;} public void setShelfLocation(String shelfLocation){this.shelfLocation=shelfLocation;}
    public BookCondition getCondition(){return condition;} public void setCondition(BookCondition condition){this.condition=condition;}
    public CopyStatus getStatus(){return status;} public void setStatus(CopyStatus status){this.status=status;}
    public String getAuditNote(){return auditNote;} public void setAuditNote(String auditNote){this.auditNote=auditNote;}
}
