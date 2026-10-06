package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface FeedbackRepository extends JpaRepository<Feedback,Long>{List<Feedback> findByMemberIdOrderByCreatedAtDesc(Long memberId);}
