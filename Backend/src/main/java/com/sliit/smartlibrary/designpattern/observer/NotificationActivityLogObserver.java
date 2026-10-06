package com.sliit.smartlibrary.designpattern.observer;

import com.sliit.smartlibrary.service.ActivityLogService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer that creates an audit trail for every Observer-pattern event.
 * This supports the Administration, Reports and Notification function.
 */
@Component
@Order(2)
public class NotificationActivityLogObserver implements LibraryObserver {

    private final ActivityLogService activityLogService;

    public NotificationActivityLogObserver(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @Override
    public void update(LibraryEvent event) {
        String details = "Source: " + event.sourceFunction()
                + " | Event: " + event.eventName()
                + " | Target user: " + event.userId()
                + " | " + event.title();

        activityLogService.log("SYSTEM-OBSERVER", "OBSERVER_NOTIFICATION", details);
    }
}
