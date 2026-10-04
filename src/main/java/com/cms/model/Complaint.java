package com.cms.model;

import java.sql.Timestamp;

public class Complaint {
    private String complaintId;
    private int userId;
    private String title;
    private String description;
    private String category;
    private String status;
    private Timestamp createdAt;
    private Integer assignedTo;      // id of the staff member handling it (null = unassigned)
    private String assignedToName;   // that staff member's full name (filled in by the DAO)

    public Complaint() {}

    public Complaint(String complaintId, int userId, String title, String description, String category) {
        this.complaintId = complaintId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = "Pending";
    }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Integer getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Integer assignedTo) { this.assignedTo = assignedTo; }
    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
}
