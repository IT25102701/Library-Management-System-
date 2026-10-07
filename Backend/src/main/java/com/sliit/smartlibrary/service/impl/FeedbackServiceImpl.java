package com.sliit.smartlibrary.service.impl;
import com.sliit.smartlibrary.validation.ValidationRules;
import com.sliit.smartlibrary.dto.FeedbackRequest;
import com.sliit.smartlibrary.entity.*;
import com.sliit.smartlibrary.enums.*;
import com.sliit.smartlibrary.exception.*;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.service.FeedbackService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;import java.util.*;
@Service
public class FeedbackServiceImpl implements FeedbackService {
 private final FeedbackRepository feedback;private final UserRepository users;
 public FeedbackServiceImpl(FeedbackRepository feedback,UserRepository users){this.feedback=feedback;this.users=users;}
 public Feedback create(Long memberId,FeedbackRequest r){if(r.getSubject()==null||r.getSubject().isBlank()||r.getMessage()==null||r.getMessage().isBlank())throw new BadRequestException("Subject and message are required.");Feedback f=new Feedback();f.setMember(users.findById(memberId).orElseThrow(()->new ResourceNotFoundException("Member not found.")));try{f.setType(FeedbackType.valueOf(r.getType().toUpperCase()));}catch(Exception e){throw new BadRequestException("Type must be FEEDBACK or COMPLAINT.");}f.setSubject(r.getSubject().trim());f.setMessage(r.getMessage().trim());f.setStatus(FeedbackStatus.OPEN);f.setCreatedAt(LocalDateTime.now());return feedback.save(f);}
 public List<Feedback> getAll(){return feedback.findAll();} public List<Feedback> forMember(Long id){return feedback.findByMemberIdOrderByCreatedAtDesc(id);}
 public Feedback updateStatus(Long id,String status){Feedback f=feedback.findById(id).orElseThrow(()->new ResourceNotFoundException("Feedback/complaint not found."));FeedbackStatus next=ValidationRules.parseEnum(FeedbackStatus.class,status,"Status");ValidationRules.requireChange(f.getStatus(),next,"record");f.setStatus(next);return feedback.save(f);}
 public void delete(Long id){feedback.delete(feedback.findById(id).orElseThrow(()->new ResourceNotFoundException("Feedback/complaint not found.")));}
}
