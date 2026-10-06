package com.sliit.smartlibrary.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="activity_logs")
public class ActivityLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(length=120) private String userEmail;
    @Column(nullable=false,length=80) private String action;
    @Column(length=1000) private String details;
    @Column(nullable=false) private LocalDateTime createdAt;
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getUserEmail(){return userEmail;} public void setUserEmail(String userEmail){this.userEmail=userEmail;}
    public String getAction(){return action;} public void setAction(String action){this.action=action;}
    public String getDetails(){return details;} public void setDetails(String details){this.details=details;}
    public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}
}
