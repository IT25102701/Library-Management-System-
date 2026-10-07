package com.sliit.smartlibrary.dto;

import jakarta.validation.constraints.*;
import com.sliit.smartlibrary.validation.ValidationPatterns;
import com.sliit.smartlibrary.enums.AcademicResourceType;

/**
 * Academic resource create/update (Book & Category Management).
 * Validated with @Valid in the controller (admin/staff API); errors return HTTP 400 with a message per field.
 */
public class AcademicResourceRequest {

    @NotBlank(message = "Resource title is required.")
    @Size(max = 220, message = "Resource title must be at most 220 characters.")
    private String title;

    @NotNull(message = "Please select a resource type.")
    private AcademicResourceType resourceType;

    @Size(max = 160, message = "Author / creator must be at most 160 characters.")
    private String author;

    @Size(max = 100, message = "Category must be at most 100 characters.")
    private String category;

    @Min(value = 1500, message = "Publication year must be a whole number from 1500 to next year.")
    private Integer publicationYear;

    @Size(max = 800, message = "Resource URL must be at most 800 characters.")
    @Pattern(regexp = ValidationPatterns.OPTIONAL_WEB_URL, message = "Resource URL must start with http:// or https://.")
    private String resourceUrl;

    @Size(max = 1800, message = "Description must be at most 1800 characters.")
    private String description;

    public String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}
    public AcademicResourceType getResourceType(){return resourceType;}
    public void setResourceType(AcademicResourceType resourceType){this.resourceType=resourceType;}
    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author=author;}
    public String getCategory(){return category;}
    public void setCategory(String category){this.category=category;}
    public Integer getPublicationYear(){return publicationYear;}
    public void setPublicationYear(Integer publicationYear){this.publicationYear=publicationYear;}
    public String getResourceUrl(){return resourceUrl;}
    public void setResourceUrl(String resourceUrl){this.resourceUrl=resourceUrl;}
    public String getDescription(){return description;}
    public void setDescription(String description){this.description=description;}
}
