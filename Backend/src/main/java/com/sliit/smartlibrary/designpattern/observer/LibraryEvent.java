package com.sliit.smartlibrary.designpattern.observer;

import com.sliit.smartlibrary.enums.NotificationType;

/**
 * Immutable event data passed from a library operation (subject) to all observers.
 */
public record LibraryEvent(
        Long userId,
        String title,
        String message,
        NotificationType notificationType,
        String sourceFunction,
        String eventName
) {
}
