package com.sliit.smartlibrary.designpattern.observer;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Concrete Subject. Spring supplies every LibraryObserver implementation and the
 * subject broadcasts each library event to all registered observers.
 */
@Component
public class LibraryNotificationSubject implements LibraryEventSubject {

    private final List<LibraryObserver> observers = new CopyOnWriteArrayList<>();

    public LibraryNotificationSubject(List<LibraryObserver> discoveredObservers) {
        observers.addAll(discoveredObservers);
    }

    @Override
    public void addObserver(LibraryObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(LibraryObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(LibraryEvent event) {
        System.out.println(
                "[OBSERVER] " + event.eventName()
                        + " from " + event.sourceFunction()
                        + " -> notifying " + observers.size() + " observer(s)"
        );

        for (LibraryObserver observer : observers) {
            try {
                observer.update(event);
            } catch (RuntimeException ex) {
                System.err.println(
                        "[OBSERVER] " + observer.getClass().getSimpleName()
                                + " failed for " + event.eventName()
                                + ": " + ex.getMessage()
                );
            }
        }
    }
}
