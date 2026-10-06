package com.sliit.smartlibrary.designpattern.observer;

import com.sliit.smartlibrary.service.NotificationService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer that stores the notification for the member and uses the
 * existing email-notification stub in NotificationServiceImpl.
 */
@Component
@Order(1)
public class InAppNotificationObserver implements LibraryObserver {

    private final NotificationService notificationService;

    public InAppNotificationObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void update(LibraryEvent event) {
        if (event.userId() == null) {
            return;
        }

        notificationService.send(
                event.userId(),
                event.title(),
                event.message(),
                event.notificationType()
        );
    }
}
