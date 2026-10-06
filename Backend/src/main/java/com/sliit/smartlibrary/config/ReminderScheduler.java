package com.sliit.smartlibrary.config;

import com.sliit.smartlibrary.designpattern.observer.LibraryEvent;
import com.sliit.smartlibrary.designpattern.observer.LibraryEventSubject;
import com.sliit.smartlibrary.entity.BorrowRecord;
import com.sliit.smartlibrary.enums.BorrowStatus;
import com.sliit.smartlibrary.enums.NotificationType;
import com.sliit.smartlibrary.repository.BorrowRecordRepository;
import com.sliit.smartlibrary.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReminderScheduler {

    private static final String FUNCTION_NAME = "Administration, Reports, and Notification Management";

    private final BorrowRecordRepository borrows;
    private final NotificationRepository notes;
    private final LibraryEventSubject eventSubject;

    public ReminderScheduler(
            BorrowRecordRepository borrows,
            NotificationRepository notes,
            LibraryEventSubject eventSubject
    ) {
        this.borrows = borrows;
        this.notes = notes;
        this.eventSubject = eventSubject;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void createDueDateAndOverdueAlerts() {
        LocalDate today = LocalDate.now();

        for (BorrowRecord borrow : borrows.findAll()) {
            if (borrow.getStatus() != BorrowStatus.ISSUED && borrow.getStatus() != BorrowStatus.OVERDUE) {
                continue;
            }

            if (borrow.getDueDate().equals(today.plusDays(1))) {
                String title = "Due date reminder";
                String message = "'" + borrow.getBookCopy().getBook().getTitle()
                        + "' is due tomorrow (" + borrow.getDueDate() + ").";

                if (!notes.existsByUserIdAndTitleAndMessage(borrow.getMember().getId(), title, message)) {
                    eventSubject.notifyObservers(new LibraryEvent(
                            borrow.getMember().getId(),
                            title,
                            message,
                            NotificationType.DUE_DATE,
                            FUNCTION_NAME,
                            "DUE_DATE_REMINDER"
                    ));
                }
            }

            if (today.isAfter(borrow.getDueDate())) {
                if (borrow.getStatus() != BorrowStatus.OVERDUE) {
                    borrow.setStatus(BorrowStatus.OVERDUE);
                    borrows.save(borrow);
                }

                String title = "Overdue book alert";
                String message = "'" + borrow.getBookCopy().getBook().getTitle()
                        + "' is overdue. Please return it as soon as possible.";

                if (!notes.existsByUserIdAndTitleAndMessage(borrow.getMember().getId(), title, message)) {
                    eventSubject.notifyObservers(new LibraryEvent(
                            borrow.getMember().getId(),
                            title,
                            message,
                            NotificationType.OVERDUE,
                            FUNCTION_NAME,
                            "OVERDUE_ALERT"
                    ));
                }
            }
        }
    }
}
