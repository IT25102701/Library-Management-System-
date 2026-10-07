package com.sliit.smartlibrary.dto;

import jakarta.validation.constraints.*;
import com.sliit.smartlibrary.validation.ValidationPatterns;

/**
 * Category create/update (Book & Category Management). Replaces the raw Category entity previously accepted.
 * Validated with @Valid in the controller (admin/staff API); errors return HTTP 400 with a message per field.
 */
public class CategoryRequest {

    @NotBlank(message = "Category name is required.")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters.")
    @Pattern(regexp = ValidationPatterns.ORG_NAME, message = "Category name can contain letters, numbers, spaces and . , ' & ( ) / + -")
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters.")
    private String description;

    public String getName(){return name;}
    public void setName(String name){this.name=name;}
    public String getDescription(){return description;}
    public void setDescription(String description){this.description=description;}
}
