package com.sliit.smartlibrary.dto;

public class InventoryAuditRequest {
    private Long bookCopyId;
    private String condition;
    private String status;
    private String shelfLocation;
    private String notes;

    public Long getBookCopyId(){return bookCopyId;}
    public void setBookCopyId(Long bookCopyId){this.bookCopyId=bookCopyId;}
    public String getCondition(){return condition;}
    public void setCondition(String condition){this.condition=condition;}
    public String getStatus(){return status;}
    public void setStatus(String status){this.status=status;}
    public String getShelfLocation(){return shelfLocation;}
    public void setShelfLocation(String shelfLocation){this.shelfLocation=shelfLocation;}
    public String getNotes(){return notes;}
    public void setNotes(String notes){this.notes=notes;}
}
