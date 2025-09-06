package com.rollingstone.dto;



import java.time.LocalDateTime;

public class Staffer {
    private long stafferId;
    private String staffNo;
    private String firstName;
    private String lastName;
    private String status; // PLANNED/ACTIVE/COMPLETED/CANCELLED
    private LocalDateTime startDt;
    private LocalDateTime endDt;

    public Staffer() {}
    public Staffer(long staffId, String staffNo, String firstName, String lastName,
                   String status, LocalDateTime startDt, LocalDateTime endDt) {
        this.stafferId = staffId; this.staffNo = staffNo;
        this.firstName = firstName; this.lastName = lastName;
        this.status = status; this.startDt = startDt; this.endDt = endDt;
    }

    public long getStafferId() { return stafferId; }
    public void setStafferId(long staffId) { this.stafferId = stafferId; }
    public String getStaffNo() { return staffNo; }
    public void setStaffNo(String staffNo) { this.staffNo = staffNo; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getStartDt() { return startDt; }
    public void setStartDt(LocalDateTime startDt) { this.startDt = startDt; }
    public LocalDateTime getEndDt() { return endDt; }
    public void setEndDt(LocalDateTime endDt) { this.endDt = endDt; }
}

