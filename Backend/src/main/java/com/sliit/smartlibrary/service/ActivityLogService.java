package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.entity.ActivityLog;
import java.util.List;
public interface ActivityLogService { ActivityLog log(String userEmail,String action,String details); List<ActivityLog> recent(); }
