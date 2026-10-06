package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.dto.BookCopyRequest;
import com.sliit.smartlibrary.entity.BookCopy;
import java.util.*;
public interface AssetService { List<BookCopy> getAll(); BookCopy get(Long id); List<BookCopy> byBook(Long bookId); long availableCount(Long bookId); BookCopy create(BookCopyRequest r); BookCopy update(Long id,BookCopyRequest r); void delete(Long id); }
