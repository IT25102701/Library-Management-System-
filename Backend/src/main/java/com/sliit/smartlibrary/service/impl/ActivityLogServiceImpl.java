package com.sliit.smartlibrary.service.impl;
import com.sliit.smartlibrary.entity.ActivityLog;
import com.sliit.smartlibrary.repository.ActivityLogRepository;
import com.sliit.smartlibrary.service.ActivityLogService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class ActivityLogServiceImpl implements ActivityLogService {
    private final ActivityLogRepository logs;
    public ActivityLogServiceImpl(ActivityLogRepository logs){this.logs=logs;}
    public ActivityLog log(String email,String action,String details){ActivityLog l=new ActivityLog();l.setUserEmail(email);l.setAction(action);l.setDetails(details);l.setCreatedAt(LocalDateTime.now());return logs.save(l);}
    public List<ActivityLog> recent(){return logs.findTop200ByOrderByCreatedAtDesc();}
}
