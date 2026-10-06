package com.sliit.smartlibrary.repository;

import com.sliit.smartlibrary.entity.InventoryAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryAuditRepository extends JpaRepository<InventoryAudit,Long> {
    List<InventoryAudit> findAllByOrderByAuditedAtDesc();
    void deleteByBookCopyId(Long bookCopyId);
}
