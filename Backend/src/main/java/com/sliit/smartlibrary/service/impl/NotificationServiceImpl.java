package com.sliit.smartlibrary.service.impl;
import com.sliit.smartlibrary.entity.*;
import com.sliit.smartlibrary.enums.NotificationType;
import com.sliit.smartlibrary.exception.ResourceNotFoundException;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.service.NotificationService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;import java.util.*;
@Service
public class NotificationServiceImpl implements NotificationService {
 private final NotificationRepository notes; private final UserRepository users;
 public NotificationServiceImpl(NotificationRepository notes,UserRepository users){this.notes=notes;this.users=users;}
 public Notification send(Long uid,String title,String message,NotificationType type){Notification n=new Notification();n.setUser(users.findById(uid).orElseThrow(()->new ResourceNotFoundException("User not found.")));n.setTitle(title);n.setMessage(message);n.setType(type);n.setCreatedAt(LocalDateTime.now());n.setReadFlag(false);Notification saved=notes.save(n);System.out.println("[EMAIL-NOTIFICATION STUB] To: "+saved.getUser().getEmail()+" | "+title+" | "+message);return saved;}
 public List<Notification> forUser(Long uid){return notes.findByUserIdOrderByCreatedAtDesc(uid);}
 public Notification markRead(Long id,Long uid){Notification n=notes.findById(id).orElseThrow(()->new ResourceNotFoundException("Notification not found."));if(!n.getUser().getId().equals(uid))throw new ResourceNotFoundException("Notification not found.");n.setReadFlag(true);return notes.save(n);}
}
