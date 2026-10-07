// This file belongs to the package com.sliit.smartlibrary.service (its folder in the project).
package com.sliit.smartlibrary.service;

// Imports the BorrowRecord entity (a database table).
import com.sliit.smartlibrary.entity.BorrowRecord;
// Imports LocalDate, a date without a time (e.g. a due date).
import java.time.LocalDate;
// Imports Java's collection classes (List, Map, Optional, ...).
import java.util.*;

// Interface BorrowService: declares the methods its implementing class must provide.
public interface BorrowService {
    // Declares issue(): lends a copy to a member (staffId = the librarian or admin doing it).
    BorrowRecord issue(Long memberId, Long copyId, Long staffId);
    // Declares getAll(): returns every borrow record.
    List<BorrowRecord> getAll();
    // Declares forMember(): returns one member's borrow history.
    List<BorrowRecord> forMember(Long memberId);
    // Declares renew(): extends the due date of a loan.
    BorrowRecord renew(Long id);
    // Declares returnBook(): records that a borrowed copy came back (and creates a fine if late).
    BorrowRecord returnBook(Long id);
    // Declares cancel(): cancels a loan that was issued by mistake.
    BorrowRecord cancel(Long id);
    // Declares borrowOnline(): a member borrows a book online; an available copy is chosen automatically.
    BorrowRecord borrowOnline(Long memberId, Long bookId);
    // Declares update(): staff edit a loan's due date and renewal count.
    BorrowRecord update(Long id, LocalDate dueDate, int renewalCount);
    // Declares delete(): staff delete a loan record (the copy goes back on the shelf if it was still out).
    void delete(Long id);
    // Declares deleteByMember(): a member deletes one of their own borrowings (only while it is ISSUED, never renewed and has no fine).
    void deleteByMember(Long id, Long memberId);
} // end of interface BorrowService
