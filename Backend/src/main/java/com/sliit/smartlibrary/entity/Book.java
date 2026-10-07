package com.sliit.smartlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity @Table(name="books")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Book {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @NotBlank @Column(nullable=false,length=180) private String title;
    @NotBlank @Column(nullable=false,unique=true,length=30) private String isbn;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="category_id") private Category category;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="author_id") private Author author;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="publisher_id") private Publisher publisher;
    @Column(length=1500) private String description;
    private Integer publishedYear;
    @Column(length=500) private String coverUrl;

    public Book(){}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getTitle(){return title;} public void setTitle(String title){this.title=title;}
    public String getIsbn(){return isbn;} public void setIsbn(String isbn){this.isbn=isbn;}
    public Category getCategory(){return category;} public void setCategory(Category category){this.category=category;}
    public Author getAuthor(){return author;} public void setAuthor(Author author){this.author=author;}
    public Publisher getPublisher(){return publisher;} public void setPublisher(Publisher publisher){this.publisher=publisher;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public Integer getPublishedYear(){return publishedYear;} public void setPublishedYear(Integer publishedYear){this.publishedYear=publishedYear;}
    public String getCoverUrl(){return coverUrl;} public void setCoverUrl(String coverUrl){this.coverUrl=coverUrl;}
}
