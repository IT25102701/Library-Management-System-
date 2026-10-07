// This file belongs to the package com.sliit.smartlibrary.controller (its folder in the project).
package com.sliit.smartlibrary.controller;

// Imports every class in the project's dto package.
import com.sliit.smartlibrary.dto.*;
// Imports every class in the project's entity package.
import com.sliit.smartlibrary.entity.*;
// Imports the BadRequestException exception.
import com.sliit.smartlibrary.exception.BadRequestException;
// Imports every class in the project's service package.
import com.sliit.smartlibrary.service.*;
// Imports the @Valid annotation, which tells Spring to check a request body against its validation rules.
import jakarta.validation.Valid;
// Imports Authentication, which describes the currently logged-in user.
import org.springframework.security.core.Authentication;
// Imports Spring web annotations (@RestController, @GetMapping, @RequestBody, ...).
import org.springframework.web.bind.annotation.*;

// Marks this class as a REST controller: its methods answer HTTP requests with JSON.
@RestController
// Every URL in this controller starts with "/api/member".
@RequestMapping("/api/member")
// Class MemberController.
public class MemberController {

    // The user service this class depends on (given to this class by Spring).
    private final UserService users;
    // The reservation service this class depends on (given to this class by Spring).
    private final ReservationService reservations;
    // The borrow service this class depends on (given to this class by Spring).
    private final BorrowService borrows;
    // The fine service this class depends on (given to this class by Spring).
    private final FineService fines;
    // The feedback service this class depends on (given to this class by Spring).
    private final FeedbackService feedback;
    // The review service this class depends on (given to this class by Spring).
    private final ReviewService reviews;
    // The notification service this class depends on (given to this class by Spring).
    private final NotificationService notifications;
    // The wishlist service this class depends on (given to this class by Spring).
    private final WishlistService wishlist;

    // Constructor: Spring passes in the dependencies listed below (dependency injection).
    public MemberController(
        // the user service,
        UserService users,
        // the reservation service,
        ReservationService reservations,
        // the borrow service,
        BorrowService borrows,
        // the fine service,
        FineService fines,
        // the feedback service,
        FeedbackService feedback,
        // the review service,
        ReviewService reviews,
        // the notification service,
        NotificationService notifications,
        // and the wishlist service.
        WishlistService wishlist
    // End of the parameter list; the constructor body starts here.
    ) {
        // Stores the given users in this object's field.
        this.users = users;
        // Stores the given reservations in this object's field.
        this.reservations = reservations;
        // Stores the given borrows in this object's field.
        this.borrows = borrows;
        // Stores the given fines in this object's field.
        this.fines = fines;
        // Stores the given feedback in this object's field.
        this.feedback = feedback;
        // Stores the given reviews in this object's field.
        this.reviews = reviews;
        // Stores the given notifications in this object's field.
        this.notifications = notifications;
        // Stores the given wishlist in this object's field.
        this.wishlist = wishlist;
    } // end of block

    // Helper: loads the account of the member who is logged in.
    private User me(Authentication a) {
        // Look the user up by the email Spring Security stored at login.
        return users.getByEmail(a.getName());
    } // end of me()

    // Handles HTTP GET requests to "/profile" (reading data).
    @GetMapping("/profile")
    // GET /api/member/profile: returns the logged-in member's account.
    public Object profile(Authentication a) {
        // Load and return it.
        return me(a);
    } // end of profile()

    // Handles HTTP PUT requests to "/profile" (updating something).
    @PutMapping("/profile")
    // PUT /api/member/profile: the member updates their own name and phone.
    public Object updateProfile(Authentication a, @RequestBody ProfileUpdateRequest r) {
        // Load the logged-in member.
        User u = me(a);
        // Save the new name and phone for this member only.
        return users.updateProfile(u.getId(), r.getName(), r.getPhone());
    } // end of updateProfile()

    // Handles HTTP PUT requests to "/password" (updating something).
    @PutMapping("/password")
    // PUT /api/member/password: the member changes their password.
    public void changePassword(Authentication a, @RequestBody PasswordChangeRequest r) {
        // Check the current password and save the new one (for this member only).
        users.changePassword(me(a).getId(), r.getCurrentPassword(), r.getNewPassword());
    } // end of changePassword()

    // Handles HTTP GET requests to "/wishlist" (reading data).
    @GetMapping("/wishlist")
    // GET /api/member/wishlist: the member's wishlist.
    public Object wishlist(Authentication a) {
        // Load the logged-in member's wishlist.
        return wishlist.forMember(me(a).getId());
    } // end of wishlist()

    // Handles HTTP POST requests to "/wishlist/{bookId}" (creating something).
    @PostMapping("/wishlist/{bookId}")
    // POST /api/member/wishlist/{bookId}: adds a book to the wishlist.
    public Object addWishlist(Authentication a, @PathVariable Long bookId) {
        // Add the book from the URL to the logged-in member's wishlist.
        return wishlist.add(me(a).getId(), bookId);
    } // end of addWishlist()

    // Handles HTTP DELETE requests to "/wishlist/{bookId}" (removing something).
    @DeleteMapping("/wishlist/{bookId}")
    // DELETE /api/member/wishlist/{bookId}: removes a book from the wishlist.
    public void removeWishlist(Authentication a, @PathVariable Long bookId) {
        // Remove the book from the logged-in member's wishlist.
        wishlist.remove(me(a).getId(), bookId);
    } // end of removeWishlist()

    // Handles HTTP POST requests to "/reservations" (creating something).
    @PostMapping("/reservations")
    // POST /api/member/reservations: the member reserves a book.
    public Object reserve(Authentication a, @RequestBody ReservationRequest r) {
        // Reserve the chosen book for the logged-in member.
        return reservations.reserve(me(a).getId(), r.getBookId());
    } // end of reserve()

    // Handles HTTP GET requests to "/reservations" (reading data).
    @GetMapping("/reservations")
    // GET /api/member/reservations: the member's reservations.
    public Object reservations(Authentication a) {
        // Load the logged-in member's reservations.
        return reservations.forMember(me(a).getId());
    } // end of reservations()

    // Handles HTTP PUT requests to "/reservations/{id}/cancel" (updating something).
    @PutMapping("/reservations/{id}/cancel")
    // PUT /api/member/reservations/{id}/cancel: the member cancels a reservation.
    public Object cancelReservation(Authentication a, @PathVariable Long id) {
        // Cancel it (the service checks it belongs to this member).
        return reservations.cancel(id, me(a).getId());
    } // end of cancelReservation()

    // Handles HTTP GET requests to "/borrowings" (reading data).
    @GetMapping("/borrowings")
    // GET /api/member/borrowings: the member's borrowing history.
    public Object borrowings(Authentication a) {
        // Load the logged-in member's loans.
        return borrows.forMember(me(a).getId());
    } // end of borrowings()

    // Handles HTTP PUT requests to "/borrowings/{id}/renew" (updating something).
    @PutMapping("/borrowings/{id}/renew")
    // PUT /api/member/borrowings/{id}/renew: the member renews a loan.
    public Object renew(Authentication a, @PathVariable Long id) {
        // Get the logged-in member's id.
        Long uid = me(a).getId();
        // Check the loan belongs to this member: take their loans...
        boolean owns = borrows
            // ...(this member's)...
            .forMember(uid)
            // ...one by one...
            .stream()
            // ...and look for one with this id.
            .anyMatch(b -> b.getId().equals(id));
        // If it is not theirs, reject with this message (HTTP 400).
        if (!owns) throw new BadRequestException("You can only renew your own borrowing record.");
        // Renew the loan (the service checks limits and due dates).
        return borrows.renew(id);
    } // end of renew()

    // Handles HTTP POST requests to "/borrowings" (creating something).
    @PostMapping("/borrowings")
    // POST /api/member/borrowings: the member borrows a book online; the body is checked first (@Valid).
    public Object borrowOnline(Authentication a, @Valid @RequestBody OnlineBorrowRequest r) {
        // Borrow it for the logged-in member (the service picks an available copy and checks the limits).
        return borrows.borrowOnline(me(a).getId(), r.getBookId());
    } // end of borrowOnline()

    // Handles HTTP DELETE requests to "/borrowings/{id}" (removing something).
    @DeleteMapping("/borrowings/{id}")
    // DELETE /api/member/borrowings/{id}: the member deletes one of their own borrowings.
    public void deleteMyBorrow(Authentication a, @PathVariable Long id) {
        // Delete it for the logged-in member (the service checks ownership, status, renewals and fines).
        borrows.deleteByMember(id, me(a).getId());
    } // end of deleteMyBorrow()

    // Handles HTTP GET requests to "/fines" (reading data).
    @GetMapping("/fines")
    // GET /api/member/fines: the member's fines.
    public Object fines(Authentication a) {
        // Load the logged-in member's fines.
        return fines.forMember(me(a).getId());
    } // end of fines()

    // Handles HTTP POST requests to "/feedback" (creating something).
    @PostMapping("/feedback")
    // POST /api/member/feedback: the member sends feedback or a complaint.
    public Object feedback(Authentication a, @RequestBody FeedbackRequest r) {
        // Save it for the logged-in member.
        return feedback.create(me(a).getId(), r);
    } // end of feedback()

    // Handles HTTP GET requests to "/feedback" (reading data).
    @GetMapping("/feedback")
    // GET /api/member/feedback: the member's feedback history.
    public Object feedback(Authentication a) {
        // Load the logged-in member's feedback.
        return feedback.forMember(me(a).getId());
    } // end of feedback()

    // Handles HTTP POST requests to "/reviews" (creating something).
    @PostMapping("/reviews")
    // POST /api/member/reviews: the member rates and reviews a book.
    public Object review(Authentication a, @RequestBody ReviewRequest r) {
        // Save the review for the logged-in member.
        return reviews.save(me(a).getId(), r);
    } // end of review()

    // Handles HTTP GET requests to "/notifications" (reading data).
    @GetMapping("/notifications")
    // GET /api/member/notifications: the member's notifications.
    public Object notifications(Authentication a) {
        // Load the logged-in member's notifications.
        return notifications.forUser(me(a).getId());
    } // end of notifications()

    // Handles HTTP PUT requests to "/notifications/{id}/read" (updating something).
    @PutMapping("/notifications/{id}/read")
    // PUT /api/member/notifications/{id}/read: marks a notification as read.
    public Object read(Authentication a, @PathVariable Long id) {
        // Mark it read (the service checks it belongs to this member).
        return notifications.markRead(id, me(a).getId());
    } // end of read()
} // end of class MemberController
