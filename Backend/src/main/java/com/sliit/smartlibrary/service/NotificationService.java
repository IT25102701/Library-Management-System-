package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.entity.Notification;
import com.sliit.smartlibrary.enums.NotificationType;
import java.util.*;
public interface NotificationService { Notification send(Long userId,String title,String message,NotificationType type); List<Notification> forUser(Long userId); Notification markRead(Long id,Long userId); }
