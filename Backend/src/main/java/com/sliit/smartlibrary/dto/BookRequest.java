package com.sliit.smartlibrary.dto;

import jakarta.validation.constraints.*;
import com.sliit.smartlibrary.validation.ValidationPatterns;
import com.sliit.smartlibrary.validation.ValidIsbn;

/**
 * Book create/update (Book & Category Management).
 * Validated with @Valid in the controller (admin/staff API); errors return HTTP 400 with a message per field.
 */
public class BookRequest {

    private Long id;

    @NotBlank(message = "Book title is required.")
    @Size(max = 180, message = "Book title must be at most 180 characters.")
    private String title;

    @NotBlank(message = "ISBN is required.")
    @Size(max = 30, message = "ISBN must be at most 30 characters.")
    @ValidIsbn
    private String isbn;

    @NotNull(message = "Please select a category.")
    @Positive(message = "Please select a category.")
    private Long categoryId;

    @NotNull(message = "Please select an author.")
    @Positive(message = "Please select an author.")
    private Long authorId;

    @Positive(message = "Please select a valid publisher.")
    private Long publisherId;

    @Size(max = 1500, message = "Description must be at most 1500 characters.")
    private String description;

    @Min(value = 1450, message = "Published year must be a whole number from 1450 to next year.")
    private Integer publishedYear;

    @Size(max = 500, message = "Cover image URL must be at most 500 characters.")
    @Pattern(regexp = ValidationPatterns.OPTIONAL_COVER_URL, message = "Cover image must be a web address (http/https) or a path starting with assets/.")
    private String coverUrl;

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getTitle(){return title;}
    public void setTitle(String title){this.title=title;}
    public String getIsbn(){return isbn;}
    public void setIsbn(String isbn){this.isbn=isbn;}
    public Long getCategoryId(){return categoryId;}
    public void setCategoryId(Long categoryId){this.categoryId=categoryId;}
    public Long getAuthorId(){return authorId;}
    public void setAuthorId(Long authorId){this.authorId=authorId;}
    public Long getPublisherId(){return publisherId;}
    public void setPublisherId(Long publisherId){this.publisherId=publisherId;}
    public String getDescription(){return description;}
    public void setDescription(String description){this.description=description;}
    public Integer getPublishedYear(){return publishedYear;}
    public void setPublishedYear(Integer publishedYear){this.publishedYear=publishedYear;}
    public String getCoverUrl(){return coverUrl;}
    public void setCoverUrl(String coverUrl){this.coverUrl=coverUrl;}
}
