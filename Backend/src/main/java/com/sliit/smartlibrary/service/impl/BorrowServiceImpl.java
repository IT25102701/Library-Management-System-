package com.sliit.smartlibrary.service.impl;

import com.sliit.smartlibrary.designpattern.observer.LibraryEvent;
import com.sliit.smartlibrary.designpattern.observer.LibraryEventSubject;
import com.sliit.smartlibrary.entity.BookCopy;
import com.sliit.smartlibrary.entity.BorrowRecord;
import com.sliit.smartlibrary.entity.Fine;
import com.sliit.smartlibrary.entity.User;
import com.sliit.smartlibrary.enums.AccountStatus;
import com.sliit.smartlibrary.enums.BookCondition;
import com.sliit.smartlibrary.enums.BorrowStatus;
import com.sliit.smartlibrary.enums.CopyStatus;
import com.sliit.smartlibrary.enums.NotificationType;
import com.sliit.smartlibrary.enums.Role;
import com.sliit.smartlibrary.exception.BadRequestException;
import com.sliit.smartlibrary.exception.ResourceNotFoundException;
import com.sliit.smartlibrary.repository.BookCopyRepository;
import com.sliit.smartlibrary.repository.BorrowRecordRepository;
import com.sliit.smartlibrary.repository.UserRepository;
import com.sliit.smartlibrary.service.BorrowService;
import com.sliit.smartlibrary.service.FineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {

    private static final String FUNCTION_NAME = "Borrowing and Issuing Management";

    private final BorrowRecordRepository borrows;
    private final UserRepository users;
    private final BookCopyRepository copies;
    private final FineService fines;
    private final LibraryEventSubject eventSubject;
    private final int borrowLimit;
    private final int maxRenewals;
    private final int loanDays;

    public BorrowServiceImpl(
            BorrowRecordRepository borrows,
            UserRepository users,
            BookCopyRepository copies,
            FineService fines,
            LibraryEventSubject eventSubject,
            @Value("${library.borrow-limit:5}") int borrowLimit,
            @Value("${library.max-renewals:2}") int maxRenewals,
            @Value("${library.loan-days:14}") int loanDays
    ) {
        this.borrows = borrows;
        this.users = users;
        this.copies = copies;
        this.fines = fines;
        this.eventSubject = eventSubject;
        this.borrowLimit = borrowLimit;
        this.maxRenewals = maxRenewals;
        this.loanDays = loanDays;
    }

    @Override
    @Transactional
    public BorrowRecord issue(Long memberId, Long copyId, Long staffId) {
        User member = users.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found."));

        if (member.getRole() != Role.MEMBER || member.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Select an active library member.");
        }

        if (borrows.countByMemberIdAndStatusIn(
                memberId,
                List.of(BorrowStatus.ISSUED, BorrowStatus.OVERDUE)
        ) >= borrowLimit) {
            throw new BadRequestException("Borrow limit reached (" + borrowLimit + ").");
        }

        User staff = users.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found."));
        if (staff.getRole() == Role.MEMBER) {
            throw new BadRequestException("Only librarian/admin staff can issue books.");
        }

        BookCopy copy = copies.findById(copyId)
                .orElseThrow(() -> new ResourceNotFoundException("Book copy not found."));
        if (copy.getStatus() != CopyStatus.AVAILABLE) {
            throw new BadRequestException("This copy is not available for issuing.");
        }

        BorrowRecord borrow = new BorrowRecord();
        borrow.setMember(member);
        borrow.setBookCopy(copy);
        borrow.setIssuedBy(staff);
        borrow.setIssueDate(LocalDate.now());
        borrow.setDueDate(LocalDate.now().plusDays(loanDays));
        borrow.setStatus(BorrowStatus.ISSUED);
        borrow.setRenewalCount(0);

        copy.setStatus(CopyStatus.ISSUED);
        copies.save(copy);
        borrow = borrows.save(borrow);

        publish(
                memberId,
                "Book issued",
                "'" + copy.getBook().getTitle() + "' is due on " + borrow.getDueDate() + ".",
                NotificationType.BORROWING,
                "BOOK_ISSUED"
        );

        return borrow;
    }

    @Override
    public List<BorrowRecord> getAll() {
        List<BorrowRecord> list = borrows.findAll();
        for (BorrowRecord borrow : list) {
            if (borrow.getStatus() == BorrowStatus.ISSUED
                    && LocalDate.now().isAfter(borrow.getDueDate())) {
                borrow.setStatus(BorrowStatus.OVERDUE);
            }
        }
        return borrows.saveAll(list);
    }

    @Override
    public List<BorrowRecord> forMember(Long id) {
        List<BorrowRecord> list = borrows.findByMemberIdOrderByIssueDateDesc(id);
        for (BorrowRecord borrow : list) {
            if (borrow.getStatus() == BorrowStatus.ISSUED
                    && LocalDate.now().isAfter(borrow.getDueDate())) {
                borrow.setStatus(BorrowStatus.OVERDUE);
            }
        }
        return borrows.saveAll(list);
    }

    @Override
    public BorrowRecord renew(Long id) {
        BorrowRecord borrow = get(id);
        if (borrow.getStatus() != BorrowStatus.ISSUED) {
            throw new BadRequestException("Only currently issued books can be renewed.");
        }
        if (LocalDate.now().isAfter(borrow.getDueDate())) {
            throw new BadRequestException("Overdue books cannot be renewed.");
        }
        if (borrow.getRenewalCount() >= maxRenewals) {
            throw new BadRequestException("Maximum renewal count reached (" + maxRenewals + ").");
        }

        borrow.setDueDate(borrow.getDueDate().plusDays(loanDays));
        borrow.setRenewalCount(borrow.getRenewalCount() + 1);
        BorrowRecord saved = borrows.save(borrow);

        publish(
                saved.getMember().getId(),
                "Borrow renewed",
                "New due date for '" + saved.getBookCopy().getBook().getTitle() + "' is " + saved.getDueDate() + ".",
                NotificationType.DUE_DATE,
                "BORROW_RENEWED"
        );

        return saved;
    }

    @Override
    @Transactional
    public BorrowRecord returnBook(Long id) {
        BorrowRecord borrow = get(id);
        if (borrow.getStatus() == BorrowStatus.RETURNED || borrow.getStatus() == BorrowStatus.CANCELLED) {
            throw new BadRequestException("This borrow record is already closed.");
        }

        borrow.setReturnDate(LocalDate.now());
        borrow.setStatus(BorrowStatus.RETURNED);

        BookCopy copy = borrow.getBookCopy();
        if (copy.getCondition() == BookCondition.LOST) {
            copy.setStatus(CopyStatus.LOST);
        } else if (copy.getCondition() == BookCondition.DAMAGED) {
            copy.setStatus(CopyStatus.DAMAGED);
        } else {
            copy.setStatus(CopyStatus.AVAILABLE);
        }

        copies.save(copy);
        borrow = borrows.save(borrow);
        Fine fine = fines.calculateForReturn(borrow);

        publish(
                borrow.getMember().getId(),
                "Book returned",
                fine == null
                        ? "'" + copy.getBook().getTitle() + "' was returned successfully."
                        : "'" + copy.getBook().getTitle() + "' was returned. An overdue fine was created.",
                NotificationType.BORROWING,
                "BOOK_RETURNED"
        );

        return borrow;
    }

    @Override
    @Transactional
    public BorrowRecord cancel(Long id) {
        BorrowRecord borrow = get(id);
        if (borrow.getStatus() != BorrowStatus.ISSUED) {
            throw new BadRequestException("Only an issued record can be cancelled.");
        }

        borrow.setStatus(BorrowStatus.CANCELLED);
        BookCopy copy = borrow.getBookCopy();
        copy.setStatus(CopyStatus.AVAILABLE);
        copies.save(copy);
        return borrows.save(borrow);
    }

    private BorrowRecord get(Long id) {
        return borrows.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found."));
    }

    private void publish(
            Long userId,
            String title,
            String message,
            NotificationType type,
            String eventName
    ) {
        eventSubject.notifyObservers(new LibraryEvent(
                userId,
                title,
                message,
                type,
                FUNCTION_NAME,
                eventName
        ));
    }
}
