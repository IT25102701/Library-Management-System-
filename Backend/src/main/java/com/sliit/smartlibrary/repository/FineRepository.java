package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface FineRepository extends JpaRepository<Fine,Long>{
    List<Fine> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<Fine> findByBorrowRecordId(Long borrowRecordId);
}
