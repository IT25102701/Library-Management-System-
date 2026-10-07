// This file belongs to the package com.sliit.smartlibrary.dto (its folder in the project).
package com.sliit.smartlibrary.dto;

// Imports the shared validation class ValidationPatterns.
import com.sliit.smartlibrary.validation.ValidationPatterns;
// Imports Bean Validation annotations (@NotBlank, @Size, @Pattern, @Email, ...) used to check incoming data.
import jakarta.validation.constraints.*;

/**
 * Staff issue a book copy to a member (Borrowing & Issuing Management).
 * Validated with @Valid in the controller (admin/staff API); errors return HTTP 400 with a message per field.
 */
// Request object (DTO) BorrowRequest: carries the data the frontend sends to the server.
public class BorrowRequest {

    // Validation: the value must be present (not null). Error message: "Please select a library member."
    @NotNull(message = "Please select a library member.")
    // Validation: the number must be greater than zero. Error message: "Please select a library member."
    @Positive(message = "Please select a library member.")
    // Field "memberId" (Long): the id of the library member.
    private Long memberId;

    // Validation: the value must be present (not null). Error message: "Please select an available book copy."
    @NotNull(message = "Please select an available book copy.")
    // Validation: the number must be greater than zero. Error message: "Please select an available book copy."
    @Positive(message = "Please select an available book copy.")
    // Field "bookCopyId" (Long): the id of the physical copy.
    private Long bookCopyId;

    // Getter: returns the id of the library member.
    public Long getMemberId() {
        // Gives back the value of the member id field.
        return memberId;
    } // end of getMemberId()

    // Setter: changes the id of the library member to the given value.
    public void setMemberId(Long memberId) {
        // Stores the given member id in this object's field.
        this.memberId = memberId;
    } // end of setMemberId()

    // Getter: returns the id of the physical copy.
    public Long getBookCopyId() {
        // Gives back the value of the book copy id field.
        return bookCopyId;
    } // end of getBookCopyId()

    // Setter: changes the id of the physical copy to the given value.
    public void setBookCopyId(Long bookCopyId) {
        // Stores the given book copy id in this object's field.
        this.bookCopyId = bookCopyId;
    } // end of setBookCopyId()
} // end of class BorrowRequest
