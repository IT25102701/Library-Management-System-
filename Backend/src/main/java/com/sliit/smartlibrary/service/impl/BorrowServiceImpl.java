// This file belongs to the package com.sliit.smartlibrary.service.impl (its folder in the project).
package com.sliit.smartlibrary.service.impl;

// Imports the Observer-pattern type LibraryEvent.
import com.sliit.smartlibrary.designpattern.observer.LibraryEvent;
// Imports the Observer-pattern type LibraryEventSubject.
import com.sliit.smartlibrary.designpattern.observer.LibraryEventSubject;
// Imports the BookCopy entity (a database table).
import com.sliit.smartlibrary.entity.BookCopy;
// Imports the BorrowRecord entity (a database table).
import com.sliit.smartlibrary.entity.BorrowRecord;
// Imports the Fine entity (a database table).
import com.sliit.smartlibrary.entity.Fine;
// Imports the User entity (a database table).
import com.sliit.smartlibrary.entity.User;
// Imports the AccountStatus enum (a fixed list of values).
import com.sliit.smartlibrary.enums.AccountStatus;
// Imports the BookCondition enum (a fixed list of values).
import com.sliit.smartlibrary.enums.BookCondition;
// Imports the BorrowStatus enum (a fixed list of values).
import com.sliit.smartlibrary.enums.BorrowStatus;
// Imports the CopyStatus enum (a fixed list of values).
import com.sliit.smartlibrary.enums.CopyStatus;
// Imports the FineStatus enum (a fixed list of values).
import com.sliit.smartlibrary.enums.FineStatus;
// Imports the NotificationType enum (a fixed list of values).
import com.sliit.smartlibrary.enums.NotificationType;
// Imports the Role enum (a fixed list of values).
import com.sliit.smartlibrary.enums.Role;
// Imports the BadRequestException exception.
import com.sliit.smartlibrary.exception.BadRequestException;
// Imports the ResourceNotFoundException exception.
import com.sliit.smartlibrary.exception.ResourceNotFoundException;
// Imports the BookCopyRepository repository (database access).
import com.sliit.smartlibrary.repository.BookCopyRepository;
// Imports the BorrowRecordRepository repository (database access).
import com.sliit.smartlibrary.repository.BorrowRecordRepository;
// Imports the ReservationRepository repository (database access).
import com.sliit.smartlibrary.repository.ReservationRepository;
// Imports the FineRepository repository (database access).
import com.sliit.smartlibrary.repository.FineRepository;
// Imports the Reservation entity (a database table).
import com.sliit.smartlibrary.entity.Reservation;
// Imports the ReservationStatus enum (a fixed list of values).
import com.sliit.smartlibrary.enums.ReservationStatus;
// Imports the UserRepository repository (database access).
import com.sliit.smartlibrary.repository.UserRepository;
// Imports the BorrowService service.
import com.sliit.smartlibrary.service.BorrowService;
// Imports the FineService service.
import com.sliit.smartlibrary.service.FineService;
// Imports LocalDate, a date without a time (e.g. a due date).
import java.time.LocalDate;
// Imports Java's List type (an ordered collection).
import java.util.List;
// Imports @Value, which reads a setting from application.properties.
import org.springframework.beans.factory.annotation.Value;
// Imports Spring's @Service annotation (marks a business-logic class).
import org.springframework.stereotype.Service;
// Imports @Transactional, which runs a method inside one database transaction.
import org.springframework.transaction.annotation.Transactional;

// Marks this class as a Spring service: Spring creates one shared instance and injects it where needed.
@Service
// Class BorrowServiceImpl: implements BorrowService.
public class BorrowServiceImpl implements BorrowService {

    // Constant FUNCTION_NAME (String), shared by the whole class and never changed; value: "Borrowing and Issuing Management".
    private static final String FUNCTION_NAME = "Borrowing and Issuing Management";

    // Repository used to read and save borrow record records (given to this class by Spring).
    private final BorrowRecordRepository borrows;
    // Repository used to read and save user records (given to this class by Spring).
    private final UserRepository users;
    // Repository used to read and save book copy records (given to this class by Spring).
    private final BookCopyRepository copies;
    // The fine service this class depends on (given to this class by Spring).
    private final FineService fines;
    // The library event subject this class depends on (given to this class by Spring).
    private final LibraryEventSubject eventSubject;
    // Repository used to read and save reservation records (given to this class by Spring).
    private final ReservationRepository reservations;
    // Repository used to read and save fine records (given to this class by Spring).
    private final FineRepository fineRecords;
    // Constant MAX_LOAN_DAYS (int), shared by the whole class and never changed; value: 60 (the latest a due date may be set after the issue date).
    private static final int MAX_LOAN_DAYS = 60;
    // Field "borrowLimit" (int): the borrow limit.
    private final int borrowLimit;
    // Field "maxRenewals" (int): the max renewals.
    private final int maxRenewals;
    // Field "loanDays" (int): the loan days.
    private final int loanDays;

    // Constructor: Spring passes in the dependencies listed below (dependency injection).
    public BorrowServiceImpl(
        // the borrow repository,
        BorrowRecordRepository borrows,
        // the user repository (to load members and staff),
        UserRepository users,
        // the copy repository,
        BookCopyRepository copies,
        // the fine service (to create overdue fines on return),
        FineService fines,
        // the Observer-pattern subject used to announce borrowing events,
        LibraryEventSubject eventSubject,
        // the reservation repository (to fulfil a member's reservation when they borrow the book),
        ReservationRepository reservations,
        // the fine repository (to stop deleting a loan that still has a fine),
        FineRepository fineRecords,
        // the most books a member may hold at once, from application.properties (default 5),
        @Value("${library.borrow-limit:5}") int borrowLimit,
        // the most times a loan may be renewed (default 2),
        @Value("${library.max-renewals:2}") int maxRenewals,
        // and the length of a loan in days (default 14).
        @Value("${library.loan-days:14}") int loanDays
    // End of the parameter list; the constructor body starts here.
    ) {
        // Stores the given borrows in this object's field.
        this.borrows = borrows;
        // Stores the given users in this object's field.
        this.users = users;
        // Stores the given copies in this object's field.
        this.copies = copies;
        // Stores the given fines in this object's field.
        this.fines = fines;
        // Stores the given event subject in this object's field.
        this.eventSubject = eventSubject;
        // Stores the given reservation repository in this object's field.
        this.reservations = reservations;
        // Stores the given fine repository in this object's field.
        this.fineRecords = fineRecords;
        // Stores the given borrow limit in this object's field.
        this.borrowLimit = borrowLimit;
        // Stores the given max renewals in this object's field.
        this.maxRenewals = maxRenewals;
        // Stores the given loan days in this object's field.
        this.loanDays = loanDays;
    } // end of block

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Runs the next method in one database transaction: if anything fails, all its changes are undone.
    @Transactional
    // Staff issue (lend) a copy to a member; staffId is the librarian/admin doing it.
    public BorrowRecord issue(Long memberId, Long copyId, Long staffId) {
        // Load the member...
        User member = users
            // ...by id...
            .findById(memberId)
            // ...or stop with "Member not found." (HTTP 404).
            .orElseThrow(() -> new ResourceNotFoundException("Member not found."));

        // Only ACTIVE accounts with the MEMBER role can borrow...
        if (member.getRole() != Role.MEMBER || member.getStatus() != AccountStatus.ACTIVE) {
            // ...otherwise reject with this message (HTTP 400).
            throw new BadRequestException("Select an active library member.");
        } // end of if block

        // Enforce the borrow limit:
        if (
            // count this member's loans...
            borrows.countByMemberIdAndStatusIn(
                // ...for this member...
                memberId,
                // ...that are ISSUED or OVERDUE...
                List.of(BorrowStatus.ISSUED, BorrowStatus.OVERDUE)
            // ...and compare with the limit;
            ) >= borrowLimit
        // if the limit is already reached...
        ) {
            // ...reject with this message, e.g. "Borrow limit reached (5)." (HTTP 400).
            throw new BadRequestException("Borrow limit reached (" + borrowLimit + ").");
        } // end of block

        // Load the staff member doing the issuing...
        User staff = users
            // ...by id...
            .findById(staffId)
            // ...or stop with "Staff user not found." (HTTP 404).
            .orElseThrow(() -> new ResourceNotFoundException("Staff user not found."));
        // Members cannot issue books to anyone...
        if (staff.getRole() == Role.MEMBER) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("Only librarian/admin staff can issue books.");
        } // end of if block

        // Load the copy being lent...
        BookCopy copy = copies
            // ...by id...
            .findById(copyId)
            // ...or stop with "Book copy not found." (HTTP 404).
            .orElseThrow(() -> new ResourceNotFoundException("Book copy not found."));
        // Only an AVAILABLE copy can be issued...
        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            // ...otherwise reject with this message (HTTP 400).
            throw new BadRequestException("This copy is not available for issuing.");
        } // end of if block
        // A member may not borrow a second copy of a title they already have:
        if (
            // check for a loan...
            borrows.existsByMemberIdAndBookCopyBookIdAndStatusIn(
                // ...by this member...
                memberId,
                // ...of the same book...
                copy.getBook().getId(),
                // ...that is still ISSUED or OVERDUE;
                List.of(BorrowStatus.ISSUED, BorrowStatus.OVERDUE)
            ) // end of the argument list
        // if there is one...
        ) {
            // ...reject...
            throw new BadRequestException(
                // ...with this message naming the book (HTTP 400).
                "This member already has a copy of '" + copy.getBook().getTitle() + "' on loan."
            ); // end of the argument list
        } // end of block

        // Create the borrow record.
        BorrowRecord borrow = new BorrowRecord();
        // Link it to the member.
        borrow.setMember(member);
        // Link it to the copy.
        borrow.setBookCopy(copy);
        // Record which staff member issued it.
        borrow.setIssuedBy(staff);
        // The issue date is today.
        borrow.setIssueDate(LocalDate.now());
        // The due date is today + the loan length (14 days by default).
        borrow.setDueDate(LocalDate.now().plusDays(loanDays));
        // The loan starts as ISSUED.
        borrow.setStatus(BorrowStatus.ISSUED);
        // It has not been renewed yet.
        borrow.setRenewalCount(0);

        // Mark the copy as ISSUED (it is no longer on the shelf).
        copy.setStatus(CopyStatus.ISSUED);
        // Save the copy.
        copies.save(copy);
        // Save the borrow record.
        borrow = borrows.save(borrow);

        // Observer pattern: tell the member the book was issued:
        publish(
            // the member to notify,
            memberId,
            // the notification title,
            "Book issued",
            // the message with the book title and due date,
            "'" + copy.getBook().getTitle() + "' is due on " + borrow.getDueDate() + ".",
            // the notification category (BORROWING),
            NotificationType.BORROWING,
            // and the event code BOOK_ISSUED.
            "BOOK_ISSUED"
        ); // end of the argument list

        // Return the saved borrow record.
        return borrow;
    } // end of issue()

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Returns every borrow record (for staff).
    public List<BorrowRecord> getAll() {
        // Load all borrow records.
        List<BorrowRecord> list = borrows.findAll();
        // Check each one:
        for (BorrowRecord borrow : list) {
            // if it is still ISSUED but today is after its due date...
            if (borrow.getStatus() == BorrowStatus.ISSUED && LocalDate.now().isAfter(borrow.getDueDate())) {
                // ...mark it as OVERDUE.
                borrow.setStatus(BorrowStatus.OVERDUE);
            } // end of if block
        } // end of for block
        // Save any changes and return the list.
        return borrows.saveAll(list);
    } // end of getAll()

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Returns one member's borrow history.
    public List<BorrowRecord> forMember(Long id) {
        // Load the member's loans, newest first.
        List<BorrowRecord> list = borrows.findByMemberIdOrderByIssueDateDesc(id);
        // Check each one:
        for (BorrowRecord borrow : list) {
            // if it is still ISSUED but today is after its due date...
            if (borrow.getStatus() == BorrowStatus.ISSUED && LocalDate.now().isAfter(borrow.getDueDate())) {
                // ...mark it as OVERDUE.
                borrow.setStatus(BorrowStatus.OVERDUE);
            } // end of if block
        } // end of for block
        // Save any changes and return the list.
        return borrows.saveAll(list);
    } // end of forMember()

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Renews a loan (gives the member more time).
    public BorrowRecord renew(Long id) {
        // Load the loan (or fail with "not found").
        BorrowRecord borrow = get(id);
        // Only ISSUED loans can be renewed...
        if (borrow.getStatus() != BorrowStatus.ISSUED) {
            // ...otherwise reject with this message (HTTP 400).
            throw new BadRequestException("Only currently issued books can be renewed.");
        } // end of if block
        // Inactive accounts cannot renew...
        if (borrow.getMember().getStatus() != AccountStatus.ACTIVE) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("Loans cannot be renewed for an inactive account.");
        } // end of if block
        // Loans that are already past their due date cannot be renewed...
        if (LocalDate.now().isAfter(borrow.getDueDate())) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("Overdue books cannot be renewed.");
        } // end of if block
        // Each loan can be renewed only a limited number of times...
        if (borrow.getRenewalCount() >= maxRenewals) {
            // ...reject with this message once the limit is reached (HTTP 400).
            throw new BadRequestException("Maximum renewal count reached (" + maxRenewals + ").");
        } // end of if block

        // Move the due date forward by one loan period.
        borrow.setDueDate(borrow.getDueDate().plusDays(loanDays));
        // Add 1 to the renewal count.
        borrow.setRenewalCount(borrow.getRenewalCount() + 1);
        // Save the change.
        BorrowRecord saved = borrows.save(borrow);

        // Observer pattern: tell the member the new due date:
        publish(
            // the member to notify,
            saved.getMember().getId(),
            // the notification title,
            "Borrow renewed",
            // the message: "New due date for '"...
            "New due date for '" +
                // ...the book title...
                saved.getBookCopy().getBook().getTitle() +
                // ..."' is "...
                "' is " +
                // ...the new due date...
                saved.getDueDate() +
                // ...and a full stop,
                ".",
            // the notification category (DUE_DATE),
            NotificationType.DUE_DATE,
            // and the event code BORROW_RENEWED.
            "BORROW_RENEWED"
        ); // end of the argument list

        // Return the saved loan.
        return saved;
    } // end of renew()

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Runs the next method in one database transaction: if anything fails, all its changes are undone.
    @Transactional
    // Records that a borrowed copy has come back.
    public BorrowRecord returnBook(Long id) {
        // Load the loan (or fail with "not found").
        BorrowRecord borrow = get(id);
        // A loan that is already RETURNED or CANCELLED cannot be returned again...
        if (borrow.getStatus() == BorrowStatus.RETURNED || borrow.getStatus() == BorrowStatus.CANCELLED) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("This borrow record is already closed.");
        } // end of if block

        // The return date is today.
        borrow.setReturnDate(LocalDate.now());
        // Mark the loan as RETURNED.
        borrow.setStatus(BorrowStatus.RETURNED);

        // Get the physical copy that came back.
        BookCopy copy = borrow.getBookCopy();
        // If the copy is recorded as LOST...
        if (copy.getCondition() == BookCondition.LOST) {
            // ...its status becomes LOST;
            copy.setStatus(CopyStatus.LOST);
        // if it is recorded as DAMAGED...
        } else if (copy.getCondition() == BookCondition.DAMAGED) {
            // ...its status becomes DAMAGED;
            copy.setStatus(CopyStatus.DAMAGED);
        // otherwise...
        } else {
            // ...it goes back on the shelf as AVAILABLE.
            copy.setStatus(CopyStatus.AVAILABLE);
        } // end of else block

        // Save the copy.
        copies.save(copy);
        // Save the loan.
        borrow = borrows.save(borrow);
        // Create an overdue fine if the book came back late (null if it was on time).
        Fine fine = fines.calculateForReturn(borrow);

        // Observer pattern: confirm the return to the member:
        publish(
            // the member to notify,
            borrow.getMember().getId(),
            // the notification title,
            "Book returned",
            // the message depends on whether a fine was created:
            fine == null
                // no fine: "...was returned successfully."
                ? "'" + copy.getBook().getTitle() + "' was returned successfully."
                // fine: "...was returned. An overdue fine was created."
                : "'" + copy.getBook().getTitle() + "' was returned. An overdue fine was created.",
            // the notification category (BORROWING),
            NotificationType.BORROWING,
            // and the event code BOOK_RETURNED.
            "BOOK_RETURNED"
        ); // end of the argument list

        // Return the saved loan.
        return borrow;
    } // end of returnBook()

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Runs the next method in one database transaction: if anything fails, all its changes are undone.
    @Transactional
    // Cancels a loan that was issued by mistake.
    public BorrowRecord cancel(Long id) {
        // Load the loan (or fail with "not found").
        BorrowRecord borrow = get(id);
        // Only an ISSUED loan can be cancelled...
        if (borrow.getStatus() != BorrowStatus.ISSUED) {
            // ...otherwise reject with this message (HTTP 400).
            throw new BadRequestException("Only an issued record can be cancelled.");
        } // end of if block

        // Mark the loan as CANCELLED.
        borrow.setStatus(BorrowStatus.CANCELLED);
        // Get the copy that was lent.
        BookCopy copy = borrow.getBookCopy();
        // Put the copy back on the shelf as AVAILABLE.
        copy.setStatus(CopyStatus.AVAILABLE);
        // Save the copy.
        copies.save(copy);
        // Save the loan and return it.
        return borrows.save(borrow);
    } // end of cancel()

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Runs the next method in one database transaction: if anything fails, all its changes are undone.
    @Transactional
    // A member borrows a book online (CREATE in CRUD, member side): the system picks an available copy.
    public BorrowRecord borrowOnline(Long memberId, Long bookId) {
        // Load the member...
        User member = users
            // ...by id...
            .findById(memberId)
            // ...or stop with "Member not found." (HTTP 404).
            .orElseThrow(() -> new ResourceNotFoundException("Member not found."));
        // Only ACTIVE accounts with the MEMBER role can borrow online...
        if (member.getRole() != Role.MEMBER || member.getStatus() != AccountStatus.ACTIVE) {
            // ...otherwise reject with this message (HTTP 400).
            throw new BadRequestException("Only active library members can borrow books online.");
        } // end of if block
        // Enforce the borrow limit: if the member already holds that many ISSUED or OVERDUE loans...
        if (borrows.countByMemberIdAndStatusIn(memberId, List.of(BorrowStatus.ISSUED, BorrowStatus.OVERDUE)) >= borrowLimit) {
            // ...reject with this message, e.g. "Borrow limit reached (5)." (HTTP 400).
            throw new BadRequestException("Borrow limit reached (" + borrowLimit + ").");
        } // end of if block
        // A member may not borrow a second copy of a title they already have on loan...
        if (borrows.existsByMemberIdAndBookCopyBookIdAndStatusIn(memberId, bookId, List.of(BorrowStatus.ISSUED, BorrowStatus.OVERDUE))) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("You already have a copy of this book on loan.");
        } // end of if block
        // Pick the first copy of this book that is AVAILABLE...
        BookCopy copy = copies
            // ...from the copies of this book...
            .findFirstByBookIdAndStatus(bookId, CopyStatus.AVAILABLE)
            // ...or reject when none is on the shelf (HTTP 400).
            .orElseThrow(() -> new BadRequestException("No copy of this book is available right now. You can reserve it instead."));

        // Create the borrow record.
        BorrowRecord borrow = new BorrowRecord();
        // Link it to the member.
        borrow.setMember(member);
        // Link it to the chosen copy.
        borrow.setBookCopy(copy);
        // Online loans are self-service, so the member is recorded as the issuer.
        borrow.setIssuedBy(member);
        // The issue date is today.
        borrow.setIssueDate(LocalDate.now());
        // The due date is today + the loan length (14 days by default).
        borrow.setDueDate(LocalDate.now().plusDays(loanDays));
        // The loan starts as ISSUED.
        borrow.setStatus(BorrowStatus.ISSUED);
        // It has not been renewed yet.
        borrow.setRenewalCount(0);
        // Mark the copy as ISSUED (it is no longer on the shelf).
        copy.setStatus(CopyStatus.ISSUED);
        // Save the copy.
        copies.save(copy);
        // Save the borrow record.
        borrow = borrows.save(borrow);

        // If the member had an ACTIVE reservation for this book...
        reservations
            // ...find it...
            .findFirstByMemberIdAndBookIdAndStatus(memberId, bookId, ReservationStatus.ACTIVE)
            // ...and if there is one:
            .ifPresent(r -> {
                // mark it FULFILLED (they now have the book),
                r.setStatus(ReservationStatus.FULFILLED);
                // and save it.
                reservations.save(r);
            }); // end of the argument list

        // Observer pattern: confirm the loan to the member:
        publish(
            // the member to notify,
            memberId,
            // the notification title,
            "Book borrowed",
            // the message with the book title, copy barcode and due date,
            "You borrowed '" + copy.getBook().getTitle() + "' (copy " + copy.getBarcode() + "). Please collect it at the desk; it is due on " + borrow.getDueDate() + ".",
            // the notification category (BORROWING),
            NotificationType.BORROWING,
            // and the event code BOOK_BORROWED_ONLINE.
            "BOOK_BORROWED_ONLINE"
        ); // end of the argument list
        // Return the saved borrow record.
        return borrow;
    } // end of borrowOnline()

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Staff edit a loan: its due date and renewal count (UPDATE in CRUD, admin side).
    public BorrowRecord update(Long id, LocalDate dueDate, int renewalCount) {
        // Load the loan (or fail with "not found").
        BorrowRecord borrow = get(id);
        // Only loans that are still out (ISSUED or OVERDUE) can be edited...
        if (borrow.getStatus() != BorrowStatus.ISSUED && borrow.getStatus() != BorrowStatus.OVERDUE) {
            // ...a returned or cancelled loan is closed (HTTP 400).
            throw new BadRequestException("Only loans that are still out can be edited.");
        } // end of if block
        // The due date is required...
        if (dueDate == null) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("Due date is required.");
        } // end of if block
        // The due date cannot be before the issue date...
        if (dueDate.isBefore(borrow.getIssueDate())) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("The due date cannot be before the issue date (" + borrow.getIssueDate() + ").");
        } // end of if block
        // The due date may be at most MAX_LOAN_DAYS after the issue date...
        if (dueDate.isAfter(borrow.getIssueDate().plusDays(MAX_LOAN_DAYS))) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("The due date can be at most " + MAX_LOAN_DAYS + " days after the issue date.");
        } // end of if block
        // The renewal count must be between 0 and the renewal limit...
        if (renewalCount < 0 || renewalCount > maxRenewals) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("Renewal count must be between 0 and " + maxRenewals + ".");
        } // end of if block
        // Reject a save that changes nothing...
        if (dueDate.equals(borrow.getDueDate()) && renewalCount == borrow.getRenewalCount()) {
            // ...with this message (HTTP 400).
            throw new BadRequestException("Nothing to update: the due date and renewal count are unchanged.");
        } // end of if block
        // Apply the new due date.
        borrow.setDueDate(dueDate);
        // Apply the new renewal count.
        borrow.setRenewalCount(renewalCount);
        // The status follows the new due date: OVERDUE if it is already past, otherwise ISSUED.
        borrow.setStatus(dueDate.isBefore(LocalDate.now()) ? BorrowStatus.OVERDUE : BorrowStatus.ISSUED);
        // Save the changes.
        BorrowRecord saved = borrows.save(borrow);

        // Observer pattern: tell the member their loan changed:
        publish(
            // the member to notify,
            saved.getMember().getId(),
            // the notification title,
            "Loan updated",
            // the message with the book title and new due date,
            "Your loan of '" + saved.getBookCopy().getBook().getTitle() + "' is now due on " + saved.getDueDate() + ".",
            // the notification category (DUE_DATE),
            NotificationType.DUE_DATE,
            // and the event code BORROW_UPDATED.
            "BORROW_UPDATED"
        ); // end of the argument list
        // Return the saved loan.
        return saved;
    } // end of update()

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Runs the next method in one database transaction: if anything fails, all its changes are undone.
    @Transactional
    // Staff delete a loan record (DELETE in CRUD, admin side).
    public void delete(Long id) {
        // Load the loan (or fail with "not found").
        BorrowRecord borrow = get(id);
        // A loan that has a fine cannot be deleted (the fine points to it):
        fineRecords.findByBorrowRecordId(id).ifPresent(fine -> {
            // an UNPAID fine is money still owed...
            if (fine.getStatus() == FineStatus.UNPAID) {
                // ...so reject with this message (HTTP 400);
                throw new BadRequestException("This loan has an unpaid fine. Mark the fine PAID or WAIVED and delete it in Returns & Fines first.");
            } // end of if block
            // a PAID or WAIVED fine must be deleted first...
            throw new BadRequestException("This loan has a fine record. Delete the fine in Returns & Fines first.");
        }); // end of the argument list
        // Was the book still out (ISSUED or OVERDUE)?
        boolean stillOut = borrow.getStatus() == BorrowStatus.ISSUED || borrow.getStatus() == BorrowStatus.OVERDUE;
        // If so...
        if (stillOut) {
            // ...take the copy that was lent...
            BookCopy copy = borrow.getBookCopy();
            // ...put it back on the shelf as AVAILABLE...
            copy.setStatus(CopyStatus.AVAILABLE);
            // ...and save it.
            copies.save(copy);
        } // end of if block
        // Remember who to notify...
        Long memberId = borrow.getMember().getId();
        // ...and which book it was, before the record is gone.
        String title = borrow.getBookCopy().getBook().getTitle();
        // Delete the loan record.
        borrows.delete(borrow);
        // If the book was still out...
        if (stillOut) {
            // ...Observer pattern: tell the member the loan was removed:
            publish(
                // the member to notify,
                memberId,
                // the notification title,
                "Loan removed",
                // the message naming the book,
                "Your loan of '" + title + "' was removed by library staff.",
                // the notification category (BORROWING),
                NotificationType.BORROWING,
                // and the event code BORROW_DELETED.
                "BORROW_DELETED"
            ); // end of the argument list
        } // end of if block
    } // end of delete()

    // Says the next method replaces a method from the parent class or interface.
    @Override
    // Runs the next method in one database transaction: if anything fails, all its changes are undone.
    @Transactional
    // A member deletes one of their own borrowings (DELETE in CRUD, member side).
    public void deleteByMember(Long id, Long memberId) {
        // Load the loan (or fail with "not found").
        BorrowRecord borrow = get(id);
        // Members may only delete their own borrowings...
        if (!borrow.getMember().getId().equals(memberId)) {
            // ...otherwise reject with this message (HTTP 400).
            throw new BadRequestException("You can only delete your own borrowings.");
        } // end of if block
        // Only a borrowing that is still ISSUED (not overdue, returned or cancelled) can be deleted...
        if (borrow.getStatus() != BorrowStatus.ISSUED) {
            // ...otherwise reject with this message (HTTP 400).
            throw new BadRequestException("Only current borrowings that are not overdue can be deleted. Please return the book at the library desk.");
        } // end of if block
        // A renewed borrowing has been kept for a while, so it must be returned at the desk instead...
        if (borrow.getRenewalCount() > 0) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("A renewed borrowing cannot be deleted. Please return the book at the library desk.");
        } // end of if block
        // A borrowing with a fine cannot be deleted (the fine points to it)...
        if (fineRecords.findByBorrowRecordId(id).isPresent()) {
            // ...reject with this message (HTTP 400).
            throw new BadRequestException("This borrowing has a fine, so it cannot be deleted.");
        } // end of if block
        // Take the copy that was lent...
        BookCopy copy = borrow.getBookCopy();
        // ...put it back on the shelf as AVAILABLE...
        copy.setStatus(CopyStatus.AVAILABLE);
        // ...and save it.
        copies.save(copy);
        // Remember the book title before the record is gone.
        String title = copy.getBook().getTitle();
        // Delete the borrowing.
        borrows.delete(borrow);
        // Observer pattern: confirm it to the member (the activity-log observer also records it for staff):
        publish(
            // the member to notify,
            memberId,
            // the notification title,
            "Borrowing deleted",
            // the message naming the book,
            "You deleted your borrowing of '" + title + "'. The copy is back on the shelf.",
            // the notification category (BORROWING),
            NotificationType.BORROWING,
            // and the event code BORROW_DELETED_BY_MEMBER.
            "BORROW_DELETED_BY_MEMBER"
        ); // end of the argument list
    } // end of deleteByMember()

    // Helper: loads one loan by id.
    private BorrowRecord get(Long id) {
        // Look in the borrow records...
        return borrows
            // ...for this id...
            .findById(id)
            // ...or stop with "Borrow record not found." (HTTP 404).
            .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found."));
    } // end of get()

    // Helper that sends one library event to all observers (Observer pattern).
    private void publish(Long userId, String title, String message, NotificationType type, String eventName) {
        // Ask the subject to notify every observer...
        eventSubject.notifyObservers(
            // ...with a new event carrying the user, title, message, type, this team function and the event code.
            new LibraryEvent(userId, title, message, type, FUNCTION_NAME, eventName)
        ); // end of the argument list
    } // end of publish()
} // end of class BorrowServiceImpl
