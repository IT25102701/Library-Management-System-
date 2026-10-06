package com.sliit.smartlibrary.dto;

import com.sliit.smartlibrary.enums.AcademicResourceType;

public class AcademicResourceRequest {
    private String title;
    private AcademicResourceType resourceType;
    private String author;
    private String category;
    private Integer publicationYear;
    private String resourceUrl;
    private String description;
    public String getTitle(){return title;} public void setTitle(String title){this.title=title;}
    public AcademicResourceType getResourceType(){return resourceType;} public void setResourceType(AcademicResourceType resourceType){this.resourceType=resourceType;}
    public String getAuthor(){return author;} public void setAuthor(String author){this.author=author;}
    public String getCategory(){return category;} public void setCategory(String category){this.category=category;}
    public Integer getPublicationYear(){return publicationYear;} public void setPublicationYear(Integer publicationYear){this.publicationYear=publicationYear;}
    public String getResourceUrl(){return resourceUrl;} public void setResourceUrl(String resourceUrl){this.resourceUrl=resourceUrl;}
    public String getDescription(){return description;} public void setDescription(String description){this.description=description;}
}
