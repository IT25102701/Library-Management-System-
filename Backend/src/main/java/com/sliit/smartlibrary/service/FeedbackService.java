package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.dto.FeedbackRequest;
import com.sliit.smartlibrary.entity.Feedback;
import java.util.*;
public interface FeedbackService { Feedback create(Long memberId,FeedbackRequest r); List<Feedback> getAll(); List<Feedback> forMember(Long memberId); Feedback updateStatus(Long id,String status); void delete(Long id); }
