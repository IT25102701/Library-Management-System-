package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.dto.ReviewRequest;
import com.sliit.smartlibrary.entity.Review;
import java.util.*;
public interface ReviewService { Review save(Long memberId,ReviewRequest r); List<Review> forBook(Long bookId); void delete(Long id,Long memberId); }
