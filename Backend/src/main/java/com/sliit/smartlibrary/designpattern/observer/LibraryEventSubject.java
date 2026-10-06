package com.sliit.smartlibrary.designpattern.observer;

/**
 * Subject role in the Observer design pattern.
 */
public interface LibraryEventSubject {
    void addObserver(LibraryObserver observer);
    void removeObserver(LibraryObserver observer);
    void notifyObservers(LibraryEvent event);
}
