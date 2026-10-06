package com.sliit.smartlibrary.dto;
public class BookCopyRequest {
 private Long bookId; private String barcode; private String qrCode; private String shelfLocation; private String condition; private String status; private String auditNote;
 public Long getBookId(){return bookId;} public void setBookId(Long bookId){this.bookId=bookId;}
 public String getBarcode(){return barcode;} public void setBarcode(String barcode){this.barcode=barcode;}
 public String getQrCode(){return qrCode;} public void setQrCode(String qrCode){this.qrCode=qrCode;}
 public String getShelfLocation(){return shelfLocation;} public void setShelfLocation(String shelfLocation){this.shelfLocation=shelfLocation;}
 public String getCondition(){return condition;} public void setCondition(String condition){this.condition=condition;}
 public String getStatus(){return status;} public void setStatus(String status){this.status=status;}
 public String getAuditNote(){return auditNote;} public void setAuditNote(String auditNote){this.auditNote=auditNote;}
}
