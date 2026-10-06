package com.sliit.smartlibrary.service;

import com.sliit.smartlibrary.dto.InventoryAuditRequest;
import com.sliit.smartlibrary.entity.InventoryAudit;
import java.util.List;
import java.util.Map;

public interface InventoryService {
    Map<String,Long> summary();
    List<InventoryAudit> getAudits();
    InventoryAudit recordAudit(InventoryAuditRequest request, String checkedBy);
    void deleteAudit(Long id);
}
