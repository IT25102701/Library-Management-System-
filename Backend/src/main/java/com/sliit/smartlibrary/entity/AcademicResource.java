package com.sliit.smartlibrary.entity;

import com.sliit.smartlibrary.enums.AcademicResourceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="academic_resources")
public class AcademicResource {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Column(nullable=false,length=220)
    private String title;

    @Enumerated(EnumType.STRING) @Column(nullable=false,length=40)
    private AcademicResourceType resourceType;

    @Column(length=160)
    private String author;

    @Column(length=100)
    private String category;

    private Integer publicationYear;

    @Column(length=800)
    private String resourceUrl;

    @Column(length=1800)
    private String description;

    @Column(nullable=false)
    private Long usageCount=0L;

    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getTitle(){return title;} public void setTitle(String title){this.title=title;}
    public AcademicResourceType getResourceType(){return resourceType;} public void setResourceType(AcademicResourceType resourceType){this.resourceType=resourceType;}
    public String getAuthor(){return author;} public void setAuthor(String author){this.author=author;}
    public String getCategory(){return category;} public void setCategory(String category){this.category=category;}
    public Integer getPublicationYear(){return publicationYear;} public void setPublicationYear(Integer publicationYear){this.publicationYear=publicationYear;}
    public String getResourceUrl(){return resourceUrl;} public void setResourceUrl(String resourceUrl){this.resourceUrl=resourceUrl;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
    public Long getUsageCount(){return usageCount;} public void setUsageCount(Long usageCount){this.usageCount=usageCount;}
}
