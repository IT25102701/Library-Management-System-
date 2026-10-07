package com.sliit.smartlibrary.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
@Entity @Table(name="authors")
public class Author {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable=false,length=120) private String name;
    @Column(length=1000) private String biography;
    public Author(){} public Author(String name){this.name=name;}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String name){this.name=name;}
    public String getBiography(){return biography;} public void setBiography(String biography){this.biography=biography;}
}
