package com.sliit.smartlibrary.designpattern.observer;

/**
 * Observer role in the Observer design pattern.
 */
public interface LibraryObserver {
    void update(LibraryEvent event);
}
