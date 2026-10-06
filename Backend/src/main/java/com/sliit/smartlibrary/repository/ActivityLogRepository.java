package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long>{List<ActivityLog> findTop200ByOrderByCreatedAtDesc();}
