package com.sliit.smartlibrary.service.impl;

import com.sliit.smartlibrary.designpattern.observer.LibraryEvent;
import com.sliit.smartlibrary.designpattern.observer.LibraryEventSubject;
import com.sliit.smartlibrary.entity.BorrowRecord;
import com.sliit.smartlibrary.entity.Fine;
import com.sliit.smartlibrary.enums.FineStatus;
import com.sliit.smartlibrary.enums.NotificationType;
import com.sliit.smartlibrary.exception.BadRequestException;
import com.sliit.smartlibrary.exception.ResourceNotFoundException;
import com.sliit.smartlibrary.repository.FineRepository;
import com.sliit.smartlibrary.service.FineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class FineServiceImpl implements FineService {

    private static final String FUNCTION_NAME = "Return and Fine Management";

    private final FineRepository fines;
    private final BigDecimal finePerDay;
    private final LibraryEventSubject eventSubject;

    public FineServiceImpl(
            FineRepository fines,
            @Value("${library.fine-per-day:50.00}") BigDecimal finePerDay,
            LibraryEventSubject eventSubject
    ) {
        this.fines = fines;
        this.finePerDay = finePerDay;
        this.eventSubject = eventSubject;
    }

    @Override
    public Fine calculateForReturn(BorrowRecord borrow) {
        if (borrow.getReturnDate() == null || !borrow.getReturnDate().isAfter(borrow.getDueDate())) {
            return null;
        }

        Optional<Fine> existing = fines.findByBorrowRecordId(borrow.getId());
        if (existing.isPresent()) {
            return existing.get();
        }

        long days = ChronoUnit.DAYS.between(borrow.getDueDate(), borrow.getReturnDate());
        Fine fine = new Fine();
        fine.setBorrowRecord(borrow);
        fine.setMember(borrow.getMember());
        fine.setAmount(finePerDay.multiply(BigDecimal.valueOf(days)));
        fine.setReason("Overdue by " + days + " day(s)");
        fine.setStatus(FineStatus.UNPAID);
        fine.setCreatedAt(LocalDateTime.now());
        Fine saved = fines.save(fine);

        publish(
                saved.getMember().getId(),
                "Overdue fine created",
                "A fine of Rs. " + saved.getAmount() + " was created for " + saved.getReason().toLowerCase() + ".",
                NotificationType.FINE,
                "FINE_CREATED"
        );

        return saved;
    }

    @Override
    public List<Fine> getAll() {
        return fines.findAll();
    }

    @Override
    public List<Fine> forMember(Long memberId) {
        return fines.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Override
    public Fine updateStatus(Long id, String status) {
        Fine fine = fines.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found."));

        try {
            fine.setStatus(FineStatus.valueOf(status.toUpperCase()));
        } catch (Exception e) {
            throw new BadRequestException("Fine status must be UNPAID, PAID or WAIVED.");
        }

        if (fine.getStatus() == FineStatus.PAID) {
            fine.setPaidAt(LocalDateTime.now());
        }

        Fine saved = fines.save(fine);
        publish(
                saved.getMember().getId(),
                "Fine status updated",
                "Your fine of Rs. " + saved.getAmount() + " is now " + saved.getStatus() + ".",
                NotificationType.FINE,
                "FINE_STATUS_CHANGED"
        );
        return saved;
    }

    @Override
    public void delete(Long id) {
        Fine fine = fines.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found."));
        fines.delete(fine);
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
