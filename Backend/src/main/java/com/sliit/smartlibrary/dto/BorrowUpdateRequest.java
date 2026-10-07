// This file belongs to the package com.sliit.smartlibrary.dto (its folder in the project).
package com.sliit.smartlibrary.dto;

// Imports Bean Validation annotations (@NotBlank, @Size, @Pattern, @Email, ...) used to check incoming data.
import jakarta.validation.constraints.*;
// Imports LocalDate, a date without a time (e.g. a due date).
import java.time.LocalDate;

/**
 * Staff edit a loan (Borrowing & Issuing Management): its due date and renewal count.
 * Validated with @Valid in the controller; errors return HTTP 400 with a message per field.
 */
// Request object (DTO) BorrowUpdateRequest: carries the data the frontend sends to the server.
public class BorrowUpdateRequest {

    // Validation: the value must be present (not null). Error message: "Due date is required."
    @NotNull(message = "Due date is required.")
    // Field "dueDate" (LocalDate): the date the book must be returned by.
    private LocalDate dueDate;

    // Validation: the value must be present (not null). Error message: "Renewal count is required."
    @NotNull(message = "Renewal count is required.")
    // Validation: the number must be at least 0. Error message: "Renewal count cannot be negative."
    @Min(value = 0, message = "Renewal count cannot be negative.")
    // Field "renewalCount" (Integer): how many times the loan has been renewed.
    private Integer renewalCount;

    // Getter: returns the date the book must be returned by.
    public LocalDate getDueDate() {
        // Gives back the value of the due date field.
        return dueDate;
    } // end of getDueDate()

    // Setter: changes the date the book must be returned by to the given value.
    public void setDueDate(LocalDate dueDate) {
        // Stores the given due date in this object's field.
        this.dueDate = dueDate;
    } // end of setDueDate()

    // Getter: returns how many times the loan has been renewed.
    public Integer getRenewalCount() {
        // Gives back the value of the renewal count field.
        return renewalCount;
    } // end of getRenewalCount()

    // Setter: changes how many times the loan has been renewed to the given value.
    public void setRenewalCount(Integer renewalCount) {
        // Stores the given renewal count in this object's field.
        this.renewalCount = renewalCount;
    } // end of setRenewalCount()
} // end of class BorrowUpdateRequest
