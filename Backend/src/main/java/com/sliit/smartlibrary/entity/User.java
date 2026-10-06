package com.sliit.smartlibrary.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sliit.smartlibrary.enums.AccountStatus;
import com.sliit.smartlibrary.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Column(nullable = false, length = 100)
    private String name;
    @Email @NotBlank @Column(nullable = false, unique = true, length = 120)
    private String email;
    @NotBlank @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 255)
    private String password;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Role role = Role.MEMBER;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;
    @Column(length = 30)
    private String phone;

    public User() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getEmail(){return email;} public void setEmail(String email){this.email=email;}
    public String getPassword(){return password;} public void setPassword(String password){this.password=password;}
    public Role getRole(){return role;} public void setRole(Role role){this.role=role;}
    public AccountStatus getStatus(){return status;} public void setStatus(AccountStatus status){this.status=status;}
    public String getPhone(){return phone;} public void setPhone(String phone){this.phone=phone;}
}
