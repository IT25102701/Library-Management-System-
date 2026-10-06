package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ReviewRepository extends JpaRepository<Review,Long>{List<Review> findByBookIdOrderByCreatedAtDesc(Long bookId);Optional<Review> findByMemberIdAndBookId(Long memberId,Long bookId);}
