package com.sliit.smartlibrary.designpattern.observer;

import com.sliit.smartlibrary.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LibraryNotificationSubjectTest {

    @Test
    void notifiesEveryRegisteredObserver() {
        List<LibraryEvent> firstReceived = new ArrayList<>();
        List<LibraryEvent> secondReceived = new ArrayList<>();

        LibraryObserver first = firstReceived::add;
        LibraryObserver second = secondReceived::add;
        LibraryNotificationSubject subject = new LibraryNotificationSubject(List.of(first, second));

        LibraryEvent event = new LibraryEvent(
                10L,
                "Due date reminder",
                "Book is due tomorrow.",
                NotificationType.DUE_DATE,
                "Administration, Reports, and Notification Management",
                "DUE_DATE_REMINDER"
        );

        subject.notifyObservers(event);

        assertEquals(1, firstReceived.size());
        assertEquals(1, secondReceived.size());
        assertEquals(event, firstReceived.get(0));
        assertEquals(event, secondReceived.get(0));
    }
}
