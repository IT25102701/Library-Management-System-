package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.Reservation;
import com.sliit.smartlibrary.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ReservationRepository extends JpaRepository<Reservation,Long>{
    List<Reservation> findByMemberIdOrderByReservedAtDesc(Long memberId);
    List<Reservation> findByStatusOrderByReservedAtAsc(ReservationStatus status);
    boolean existsByMemberIdAndBookIdAndStatus(Long memberId,Long bookId,ReservationStatus status);
}
