// This file belongs to the package com.sliit.smartlibrary.dto (its folder in the project).
package com.sliit.smartlibrary.dto;

// Imports Bean Validation annotations (@NotBlank, @Size, @Pattern, @Email, ...) used to check incoming data.
import jakarta.validation.constraints.*;

/**
 * A member borrows a book online (Borrowing & Issuing Management): the system picks an available copy.
 * Validated with @Valid in the controller; errors return HTTP 400 with a message per field.
 */
// Request object (DTO) OnlineBorrowRequest: carries the data the frontend sends to the server.
public class OnlineBorrowRequest {

    // Validation: the value must be present (not null). Error message: "Please select a book."
    @NotNull(message = "Please select a book.")
    // Validation: the number must be greater than zero. Error message: "Please select a book."
    @Positive(message = "Please select a book.")
    // Field "bookId" (Long): the id of the book.
    private Long bookId;

    // Getter: returns the id of the book.
    public Long getBookId() {
        // Gives back the value of the book id field.
        return bookId;
    } // end of getBookId()

    // Setter: changes the id of the book to the given value.
    public void setBookId(Long bookId) {
        // Stores the given book id in this object's field.
        this.bookId = bookId;
    } // end of setBookId()
} // end of class OnlineBorrowRequest
