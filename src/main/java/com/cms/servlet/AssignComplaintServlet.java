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

@WebServlet("/api/assign-complaint")
public class AssignComplaintServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");

        // Security check: Only Admin can assign complaints
        if (user == null || !"Admin".equals(user.getRole())) {
            out.write("{\"success\": false, \"message\": \"Unauthorized access.\"}");
            return;
        }

        String complaintId = request.getParameter("complaintId");
        String staffIdStr = request.getParameter("staffId");

        boolean success = false;
        if (complaintId != null && !complaintId.isEmpty()) {
            Integer staffId = (staffIdStr == null || staffIdStr.isEmpty()) ? null : Integer.parseInt(staffIdStr);

            // Calls the correct method name in ComplaintDAO
            success = ComplaintDAO.assignComplaint(complaintId, staffId);
        }

        if (success) {
            out.write("{\"success\": true, \"message\": \"Assigned successfully!\"}");
        } else {
            out.write("{\"success\": false, \"message\": \"Database update failed.\"}");
        }
        out.flush();
    }
}