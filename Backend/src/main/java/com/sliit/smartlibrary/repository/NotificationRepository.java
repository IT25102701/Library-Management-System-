package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface NotificationRepository extends JpaRepository<Notification,Long>{List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);boolean existsByUserIdAndTitleAndMessage(Long userId,String title,String message);}
