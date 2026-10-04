package com.cms.dao;

import com.cms.model.Complaint;
import com.cms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    // Safe left join that ensures unassigned complaints (assigned_to is null) render correctly[cite: 7]
    private static final String SELECT_BASE =
            "SELECT c.*, u.full_name AS staff_name FROM complaints c "
          + "LEFT JOIN users u ON c.assigned_to = u.id ";

    private static Complaint map(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        try {
            c.setComplaintId(rs.getString("complaint_id"));
            c.setUserId(rs.getInt("user_id"));
            c.setTitle(rs.getString("title"));
            c.setDescription(rs.getString("description"));
            c.setCategory(rs.getString("category"));
            c.setStatus(rs.getString("status"));
            c.setCreatedAt(rs.getTimestamp("created_at"));
            int assigned = rs.getInt("assigned_to");
            c.setAssignedTo(rs.wasNull() ? null : Integer.valueOf(assigned));
            c.setAssignedToName(rs.getString("staff_name"));
        } catch (Exception e) {
            System.err.println(">>> MAPPING ERROR EXCEPTION: " + e.getMessage());
            e.printStackTrace();
        }
        return c;
    }

    private static List<Complaint> fetch(String query, Integer param) {
        List<Complaint> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(query)) {

            if (param != null) {
                pst.setInt(1, param);
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println(">>> SQL EXCEPTION CAUGHT IN COMPLAINT DAO: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Create a complaint[cite: 7]
    public static boolean createComplaint(Complaint complaint) {
        String query = "INSERT INTO complaints (complaint_id, user_id, title, description, category) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, complaint.getComplaintId());
            pst.setInt(2, complaint.getUserId());
            pst.setString(3, complaint.getTitle());
            pst.setString(4, complaint.getDescription());
            pst.setString(5, complaint.getCategory());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(">>> SQL ERROR ON CREATE: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Student: own complaints[cite: 7]
    public static List<Complaint> getComplaintsByUser(int userId) {
        return fetch(SELECT_BASE + "WHERE c.user_id = ? ORDER BY c.created_at DESC", userId);
    }

    // Staff: complaints assigned to this staff member[cite: 7]
    public static List<Complaint> getComplaintsByStaff(int staffId) {
        return fetch(SELECT_BASE + "WHERE c.assigned_to = ? ORDER BY c.created_at DESC", staffId);
    }

    // Admin: everything[cite: 7]
    public static List<Complaint> getAllComplaints() {
        return fetch(SELECT_BASE + "ORDER BY c.created_at DESC", null);
    }

    // Admin: change status of any complaint[cite: 7]
    public static boolean updateStatus(String complaintId, String newStatus) {
        String query = "UPDATE complaints SET status = ? WHERE complaint_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, newStatus);
            pst.setString(2, complaintId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Staff: change status only if the complaint is assigned to them[cite: 7]
    public static boolean updateStatusForStaff(String complaintId, String newStatus, int staffId) {
        String query = "UPDATE complaints SET status = ? WHERE complaint_id = ? AND assigned_to = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, newStatus);
            pst.setString(2, complaintId);
            pst.setInt(3, staffId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Admin: assign a complaint to a staff member (staffId null = unassign)[cite: 7]
    public static boolean assignComplaint(String complaintId, Integer staffId) {
        String query = "UPDATE complaints SET assigned_to = ? WHERE complaint_id = ?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(query)) {

            if (staffId == null) {
                pst.setNull(1, Types.INTEGER);
            } else {
                pst.setInt(1, staffId);
            }
            pst.setString(2, complaintId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}