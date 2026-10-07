package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.BorrowRecord;
import com.sliit.smartlibrary.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord,Long>{
    List<BorrowRecord> findByMemberIdOrderByIssueDateDesc(Long memberId);
    long countByMemberIdAndStatus(Long memberId,BorrowStatus status);
    long countByMemberIdAndStatusIn(Long memberId, java.util.Collection<BorrowStatus> statuses);
    Optional<BorrowRecord> findByBookCopyIdAndStatus(Long copyId,BorrowStatus status);
}
