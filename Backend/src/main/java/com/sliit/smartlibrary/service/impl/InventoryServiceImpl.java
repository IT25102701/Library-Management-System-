package com.sliit.smartlibrary.service.impl;

import com.sliit.smartlibrary.dto.InventoryAuditRequest;
import com.sliit.smartlibrary.entity.BookCopy;
import com.sliit.smartlibrary.entity.InventoryAudit;
import com.sliit.smartlibrary.enums.BookCondition;
import com.sliit.smartlibrary.enums.CopyStatus;
import com.sliit.smartlibrary.exception.BadRequestException;
import com.sliit.smartlibrary.exception.ResourceNotFoundException;
import com.sliit.smartlibrary.repository.BookCopyRepository;
import com.sliit.smartlibrary.repository.InventoryAuditRepository;
import com.sliit.smartlibrary.service.InventoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final BookCopyRepository copies;
    private final InventoryAuditRepository audits;

    public InventoryServiceImpl(BookCopyRepository copies, InventoryAuditRepository audits){
        this.copies=copies;
        this.audits=audits;
    }

    public Map<String,Long> summary(){
        Map<String,Long> m=new LinkedHashMap<>();
        m.put("total",copies.count());
        m.put("available",copies.countByStatus(CopyStatus.AVAILABLE));
        m.put("issued",copies.countByStatus(CopyStatus.ISSUED));
        m.put("reserved",copies.countByStatus(CopyStatus.RESERVED));
        m.put("damaged",copies.countByStatus(CopyStatus.DAMAGED));
        m.put("lost",copies.countByStatus(CopyStatus.LOST));
        m.put("audits",audits.count());
        return m;
    }

    public List<InventoryAudit> getAudits(){return audits.findAllByOrderByAuditedAtDesc();}

    @Transactional
    public InventoryAudit recordAudit(InventoryAuditRequest r,String checkedBy){
        if(r.getBookCopyId()==null) throw new BadRequestException("Book copy is required for an inventory audit.");
        BookCopy copy=copies.findById(r.getBookCopyId()).orElseThrow(()->new ResourceNotFoundException("Book copy not found: "+r.getBookCopyId()));
        try{
            if(r.getCondition()!=null&&!r.getCondition().isBlank()) copy.setCondition(BookCondition.valueOf(r.getCondition().toUpperCase()));
            if(r.getStatus()!=null&&!r.getStatus().isBlank()) copy.setStatus(CopyStatus.valueOf(r.getStatus().toUpperCase()));
        }catch(IllegalArgumentException e){throw new BadRequestException("Invalid inventory condition or status.");}
        if(r.getShelfLocation()!=null&&!r.getShelfLocation().isBlank()) copy.setShelfLocation(r.getShelfLocation().trim());
        if(r.getNotes()!=null) copy.setAuditNote(r.getNotes().trim());
        copies.save(copy);

        InventoryAudit audit=new InventoryAudit();
        audit.setBookCopy(copy);
        audit.setAuditedAt(LocalDateTime.now());
        audit.setCheckedBy(checkedBy);
        audit.setCondition(copy.getCondition());
        audit.setStatus(copy.getStatus());
        audit.setShelfLocation(copy.getShelfLocation());
        audit.setNotes(r.getNotes());
        return audits.save(audit);
    }

    public void deleteAudit(Long id){
        InventoryAudit audit=audits.findById(id).orElseThrow(()->new ResourceNotFoundException("Inventory audit not found: "+id));
        audits.delete(audit);
    }
}
