package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.entity.BorrowRecord;
import java.util.*;
public interface BorrowService { BorrowRecord issue(Long memberId,Long copyId,Long staffId); List<BorrowRecord> getAll(); List<BorrowRecord> forMember(Long memberId); BorrowRecord renew(Long id); BorrowRecord returnBook(Long id); BorrowRecord cancel(Long id); }
