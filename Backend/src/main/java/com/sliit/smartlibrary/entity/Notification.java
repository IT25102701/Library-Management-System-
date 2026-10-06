package com.sliit.smartlibrary.entity;
import com.sliit.smartlibrary.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="notifications")
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="user_id") private User user;
    @Column(nullable=false,length=150) private String title;
    @Column(nullable=false,length=1000) private String message;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private NotificationType type=NotificationType.GENERAL;
    @Column(nullable=false) private Boolean readFlag=false;
    @Column(nullable=false) private LocalDateTime createdAt;
    public Notification(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public User getUser(){return user;} public void setUser(User user){this.user=user;}
    public String getTitle(){return title;} public void setTitle(String title){this.title=title;}
    public String getMessage(){return message;} public void setMessage(String message){this.message=message;}
    public NotificationType getType(){return type;} public void setType(NotificationType type){this.type=type;}
    public Boolean getReadFlag(){return readFlag;} public void setReadFlag(Boolean readFlag){this.readFlag=readFlag;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}
}
