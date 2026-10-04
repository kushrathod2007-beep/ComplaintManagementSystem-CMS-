package com.cms.servlet;

import com.cms.dao.ComplaintDAO;
import com.cms.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/update-staff-complaint")
public class UpdateStaffComplaintServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"Staff".equals(user.getRole())) {
            out.write("{\"success\": false}");
            return;
        }

        String complaintId = request.getParameter("complaintId");
        String newStatus = request.getParameter("status");

        boolean success = false;
        if (complaintId != null && newStatus != null) {
            // Uses your existing DAO method ensuring staff can only update their own
            // assigned tasks
            success = ComplaintDAO.updateStatusForStaff(complaintId, newStatus, user.getId());
        }

        if (success) {
            out.write("{\"success\": true}");
        } else {
            out.write("{\"success\": false}");
        }
        out.flush();
    }
}