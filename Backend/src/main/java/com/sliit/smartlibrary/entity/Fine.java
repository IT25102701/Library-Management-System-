package com.sliit.smartlibrary.entity;
import com.sliit.smartlibrary.enums.FineStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name="fines")
public class Fine {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="borrow_record_id",unique=true) private BorrowRecord borrowRecord;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="member_id") private User member;
    @Column(nullable=false,precision=10,scale=2) private BigDecimal amount;
    @Column(nullable=false,length=255) private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private FineStatus status=FineStatus.UNPAID;
    @Column(nullable=false) private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    public Fine(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public BorrowRecord getBorrowRecord(){return borrowRecord;} public void setBorrowRecord(BorrowRecord borrowRecord){this.borrowRecord=borrowRecord;}
    public User getMember(){return member;} public void setMember(User member){this.member=member;}
    public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal amount){this.amount=amount;}
    public String getReason(){return reason;} public void setReason(String reason){this.reason=reason;}
    public FineStatus getStatus(){return status;} public void setStatus(FineStatus status){this.status=status;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}
    public LocalDateTime getPaidAt(){return paidAt;} public void setPaidAt(LocalDateTime paidAt){this.paidAt=paidAt;}
}
