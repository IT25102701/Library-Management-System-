// This file belongs to the package com.sliit.smartlibrary.repository (its folder in the project).
package com.sliit.smartlibrary.repository;

// Imports the BorrowRecord entity (a database table).
import com.sliit.smartlibrary.entity.BorrowRecord;
// Imports the BorrowStatus enum (a fixed list of values).
import com.sliit.smartlibrary.enums.BorrowStatus;
// Imports Java's collection classes (List, Map, Optional, ...).
import java.util.*;
// Imports Spring Data's JpaRepository, which provides save, findById, findAll, delete, ... automatically.
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for BorrowRecord rows (ids of type Long). Spring Data writes the implementation automatically; JpaRepository already provides save, findById, findAll, count and delete.
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    // Spring Data query (built from the method name): Finds records where member id equals the given value, sorted by issue date (newest first).
    List<BorrowRecord> findByMemberIdOrderByIssueDateDesc(Long memberId);
    // Spring Data query (built from the method name): Counts the records where member id equals the given value and status equals the given value.
    long countByMemberIdAndStatus(Long memberId, BorrowStatus status);
    // Spring Data query (built from the method name): Counts the records where member id equals the given value and status is one of the given values.
    long countByMemberIdAndStatusIn(Long memberId, java.util.Collection<BorrowStatus> statuses);
    // Spring Data query (built from the method name): Finds records where book copy id equals the given value and status equals the given value.
    Optional<BorrowRecord> findByBookCopyIdAndStatus(Long copyId, BorrowStatus status);
    // validation support
    // Spring Data query (built from the method name): Returns true if any record exists where member id equals the given value and book copy's book id equals the given value and status is one of the given values.
    boolean existsByMemberIdAndBookCopyBookIdAndStatusIn(
        // Parameter: the member id to search for.
        Long memberId,
        // Parameter: the book id to search for.
        Long bookId,
        // Parameter: the statuses to search for.
        java.util.Collection<BorrowStatus> statuses
    ); // end of the argument list
    // Spring Data query (built from the method name): Returns true if any record exists where book copy id equals the given value and status is one of the given values.
    boolean existsByBookCopyIdAndStatusIn(Long copyId, java.util.Collection<BorrowStatus> statuses);
} // end of interface BorrowRecordRepository
