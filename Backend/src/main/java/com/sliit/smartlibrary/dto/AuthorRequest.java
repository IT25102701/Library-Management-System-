package com.sliit.smartlibrary.dto;

import jakarta.validation.constraints.*;
import com.sliit.smartlibrary.validation.ValidationPatterns;

/**
 * Author create/update (Book & Category Management). Replaces the raw Author entity previously accepted.
 * Validated with @Valid in the controller (admin/staff API); errors return HTTP 400 with a message per field.
 */
public class AuthorRequest {

    @NotBlank(message = "Author name is required.")
    @Size(min = 2, max = 120, message = "Author name must be between 2 and 120 characters.")
    @Pattern(regexp = ValidationPatterns.AUTHOR_NAME, message = "Author name can contain letters, numbers, spaces and . , ' & ( ) -")
    private String name;

    @Size(max = 1000, message = "Biography must be at most 1000 characters.")
    private String biography;

    public String getName(){return name;}
    public void setName(String name){this.name=name;}
    public String getBiography(){return biography;}
    public void setBiography(String biography){this.biography=biography;}
}
