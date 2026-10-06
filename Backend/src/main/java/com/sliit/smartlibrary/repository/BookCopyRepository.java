package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.BookCopy;
import com.sliit.smartlibrary.enums.CopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BookCopyRepository extends JpaRepository<BookCopy,Long>{
    boolean existsByBarcodeIgnoreCase(String barcode);
    boolean existsByQrCodeIgnoreCase(String qrCode);
    List<BookCopy> findByBookId(Long bookId);
    long countByBookIdAndStatus(Long bookId, CopyStatus status);
    long countByStatus(CopyStatus status);
    Optional<BookCopy> findFirstByBookIdAndStatus(Long bookId, CopyStatus status);
}
