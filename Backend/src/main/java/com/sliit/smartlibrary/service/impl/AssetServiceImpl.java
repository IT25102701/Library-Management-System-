package com.sliit.smartlibrary.service.impl;
import com.sliit.smartlibrary.dto.BookCopyRequest;
import com.sliit.smartlibrary.entity.BookCopy;
import com.sliit.smartlibrary.enums.*;
import com.sliit.smartlibrary.exception.*;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.service.AssetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class AssetServiceImpl implements AssetService {
 private final BookCopyRepository copies; private final BookRepository books; private final InventoryAuditRepository audits;
 public AssetServiceImpl(BookCopyRepository copies,BookRepository books,InventoryAuditRepository audits){this.copies=copies;this.books=books;this.audits=audits;}
 public List<BookCopy> getAll(){return copies.findAll();}
 public BookCopy get(Long id){return copies.findById(id).orElseThrow(()->new ResourceNotFoundException("Book copy not found: "+id));}
 public List<BookCopy> byBook(Long bookId){return copies.findByBookId(bookId);}
 public long availableCount(Long bookId){return copies.countByBookIdAndStatus(bookId,CopyStatus.AVAILABLE);}
 public BookCopy create(BookCopyRequest r){if(r.getBarcode()!=null&&copies.existsByBarcodeIgnoreCase(r.getBarcode()))throw new BadRequestException("This barcode already exists.");if(r.getQrCode()!=null&&!r.getQrCode().isBlank()&&copies.existsByQrCodeIgnoreCase(r.getQrCode()))throw new BadRequestException("This QR code already exists.");return copies.save(apply(new BookCopy(),r));}
 public BookCopy update(Long id,BookCopyRequest r){BookCopy c=get(id);if(r.getBarcode()!=null&&!r.getBarcode().equalsIgnoreCase(c.getBarcode())&&copies.existsByBarcodeIgnoreCase(r.getBarcode()))throw new BadRequestException("This barcode already exists.");if(r.getQrCode()!=null&&!r.getQrCode().isBlank()&&(c.getQrCode()==null||!r.getQrCode().equalsIgnoreCase(c.getQrCode()))&&copies.existsByQrCodeIgnoreCase(r.getQrCode()))throw new BadRequestException("This QR code already exists.");return copies.save(apply(c,r));}
 @Transactional public void delete(Long id){BookCopy c=get(id);audits.deleteByBookCopyId(id);copies.delete(c);}
 private BookCopy apply(BookCopy c,BookCopyRequest r){if(r.getBookId()==null)throw new BadRequestException("Book is required.");if(r.getBarcode()==null||r.getBarcode().isBlank())throw new BadRequestException("Barcode is required.");if(r.getShelfLocation()==null||r.getShelfLocation().isBlank())throw new BadRequestException("Shelf/rack location is required.");c.setBook(books.findById(r.getBookId()).orElseThrow(()->new BadRequestException("Select a valid book.")));c.setBarcode(r.getBarcode().trim());String qr=(r.getQrCode()==null||r.getQrCode().isBlank())?"QR-"+r.getBarcode().trim():r.getQrCode().trim();c.setQrCode(qr);c.setShelfLocation(r.getShelfLocation().trim());try{if(r.getCondition()!=null)c.setCondition(BookCondition.valueOf(r.getCondition().toUpperCase()));if(r.getStatus()!=null)c.setStatus(CopyStatus.valueOf(r.getStatus().toUpperCase()));}catch(Exception e){throw new BadRequestException("Invalid condition or availability status.");}c.setAuditNote(r.getAuditNote());return c;}
}
