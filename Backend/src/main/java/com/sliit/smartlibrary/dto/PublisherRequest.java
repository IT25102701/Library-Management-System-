package com.sliit.smartlibrary.dto;

import jakarta.validation.constraints.*;
import com.sliit.smartlibrary.validation.ValidationPatterns;

/**
 * Publisher create/update (Book & Category Management). Replaces the raw Publisher entity previously accepted.
 * Validated with @Valid in the controller (admin/staff API); errors return HTTP 400 with a message per field.
 */
public class PublisherRequest {

    @NotBlank(message = "Publisher name is required.")
    @Size(min = 2, max = 120, message = "Publisher name must be between 2 and 120 characters.")
    @Pattern(regexp = ValidationPatterns.ORG_NAME, message = "Publisher name can contain letters, numbers, spaces and . , ' & ( ) / + -")
    private String name;

    @Email(message = "Enter a valid email address, e.g. name@example.com.")
    @Size(max = 120, message = "Email address must be at most 120 characters.")
    private String email;

    public String getName(){return name;}
    public void setName(String name){this.name=name;}
    public String getEmail(){return email;}
    public void setEmail(String email){this.email=email;}
}
