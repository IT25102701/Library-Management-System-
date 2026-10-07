package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.entity.Fine;
import com.sliit.smartlibrary.entity.BorrowRecord;
import java.util.*;
public interface FineService { Fine calculateForReturn(BorrowRecord borrow); List<Fine> getAll(); List<Fine> forMember(Long memberId); Fine updateStatus(Long id,String status); void delete(Long id); }
