package com.sliit.smartlibrary.entity;
import com.sliit.smartlibrary.enums.FeedbackStatus;
import com.sliit.smartlibrary.enums.FeedbackType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="feedback")
public class Feedback {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="member_id") private User member;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private FeedbackType type;
    @Column(nullable=false,length=150) private String subject;
    @Column(nullable=false,length=1500) private String message;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private FeedbackStatus status=FeedbackStatus.OPEN;
    @Column(nullable=false) private LocalDateTime createdAt;
    public Feedback(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public User getMember(){return member;} public void setMember(User member){this.member=member;}
    public FeedbackType getType(){return type;} public void setType(FeedbackType type){this.type=type;}
    public String getSubject(){return subject;} public void setSubject(String subject){this.subject=subject;}
    public String getMessage(){return message;} public void setMessage(String message){this.message=message;}
    public FeedbackStatus getStatus(){return status;} public void setStatus(FeedbackStatus status){this.status=status;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}
}
