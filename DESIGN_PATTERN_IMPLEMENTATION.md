# Observer Design Pattern - Final Project Integration

## Project
**Web-Based Library Management System (SmartLibrary Connect)**  
Group: **2026-Y2-S1-MLB-B10G2-09**

## Pattern Implemented
The project now contains a real implementation of the **Observer Design Pattern** for library notifications and notification audit logging.

The pattern is not a separate sample only. It is integrated into the normal reservation, borrowing, return/fine, and reminder workflows.

## Observer Pattern Components

| Observer role | Project class | Responsibility |
|---|---|---|
| Event data | `designpattern.observer.LibraryEvent` | Carries target user, title, message, notification type, source function, and event name. |
| Subject interface | `designpattern.observer.LibraryEventSubject` | Defines `addObserver`, `removeObserver`, and `notifyObservers`. |
| Concrete Subject | `designpattern.observer.LibraryNotificationSubject` | Keeps registered observers and broadcasts every library event. |
| Observer interface | `designpattern.observer.LibraryObserver` | Defines the common `update(LibraryEvent)` operation. |
| Concrete Observer 1 | `designpattern.observer.InAppNotificationObserver` | Saves the member notification through the existing `NotificationService`; the existing email stub is also executed. |
| Concrete Observer 2 | `designpattern.observer.NotificationActivityLogObserver` | Records each Observer event in the administrator Activity Log for monitoring/auditing. |

## Functions and Members Affected

| Student | Assigned major function | How the Observer pattern is applied |
|---|---|---|
| **IT25102701 - Liyanaarachchi G. C. H.** | Book Search and Reservation Management | `ReservationServiceImpl` publishes reservation-created, reservation-status-changed, and reservation-cancelled events. |
| **IT25102278 - Koswatta A.C.W.W.M.R.** | Borrowing and Issuing Management | `BorrowServiceImpl` publishes book-issued, borrow-renewed, and book-returned events. |
| **IT25103242 - Andrewdilan T.** | Return and Fine Management | `FineServiceImpl` publishes fine-created and fine-status-changed events. The return workflow also publishes the book-returned event. |
| **IT25100725 - Fernando K.M.D.** | Administration, Reports, and Notification Management | Owns the core notification use case: `ReminderScheduler` publishes due-date and overdue events; the two concrete observers create member notifications and activity-log records. |
| **IT25101473 - Ahamed M.S.S.** | User Authentication and User Management | No direct Observer-pattern code was added to this function. Existing `User` IDs are used as notification targets. |
| **IT25100139 - Harikesh S.** | Book and Category Management | No direct Observer-pattern code was added to this function. Existing book data is used in reservation/borrowing notification messages. |

## Events Now Published Through the Pattern

- `RESERVATION_CREATED`
- `RESERVATION_STATUS_CHANGED`
- `RESERVATION_CANCELLED`
- `BOOK_ISSUED`
- `BORROW_RENEWED`
- `BOOK_RETURNED`
- `FINE_CREATED`
- `FINE_STATUS_CHANGED`
- `DUE_DATE_REMINDER`
- `OVERDUE_ALERT`

## Runtime Flow

1. A normal library operation occurs, for example a member reserves a book.
2. The relevant service creates a `LibraryEvent`.
3. The service calls `LibraryEventSubject.notifyObservers(...)`.
4. `LibraryNotificationSubject` broadcasts the event to every registered `LibraryObserver`.
5. `InAppNotificationObserver` stores the notification for the member and invokes the existing email-notification stub.
6. `NotificationActivityLogObserver` records the same event in the administrator Activity Log.
7. The original business operation continues normally.

This creates loose coupling: reservation, borrowing, return/fine, and reminder classes no longer need to know how many notification channels exist or how each channel works.

## How to Demonstrate It in the Final Presentation

1. Start SQL Server and run the backend as usual.
2. Log in as a **Member** and create a reservation.
3. Open **Member Dashboard -> Notifications** and show the generated reservation notification.
4. Log in as **Administrator** and open **Activity Logs**. Show the `OBSERVER_NOTIFICATION` entry created from the same event.
5. Log in as **Librarian**, issue a book to the member, and repeat the two checks above.
6. Renew/return the book. For an overdue return, show the fine notification as well.
7. In the IntelliJ backend console, point out the `[OBSERVER] ... -> notifying 2 observer(s)` message.

## Key Viva Explanation

> The Observer Pattern is used because several independent actions must react when a library event occurs. Reservation, borrowing, fine and reminder modules act as event publishers. `LibraryNotificationSubject` is the concrete subject. It notifies every `LibraryObserver`. `InAppNotificationObserver` saves the member notification, while `NotificationActivityLogObserver` writes an administrator audit entry. This keeps the business services loosely coupled from the notification implementations and allows another observer, such as SMS, to be added later without rewriting reservation or borrowing logic.

## Windows Run Instructions

The normal Windows workflow is unchanged:

1. Start Microsoft SQL Server.
2. If necessary, run `database/create_database.sql` in SSMS.
3. Use **JDK 17** for the project and Gradle JVM.
4. Run `SmartLibraryApplication.java` from IntelliJ, or use `RUN_LIBRARY.bat`.
5. Use the existing frontend with VS Code Live Server, or open the backend-served page as before.

To run the focused Observer unit test on Windows, double-click `TEST_OBSERVER_PATTERN.bat` after Gradle dependencies are available.
