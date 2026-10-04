package com.cms.servlet;

import com.cms.model.User;
import com.cms.dao.ComplaintDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/update-complaint")
public class UpdateComplaintServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // Only Admin and Staff may change a complaint's status
        boolean allowed = user != null
                && ("Admin".equals(user.getRole()) || "Staff".equals(user.getRole()));
        if (!allowed) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String complaintId = request.getParameter("complaintId");
        String status = request.getParameter("status");

        boolean validStatus = "Pending".equals(status)
                || "In Progress".equals(status)
                || "Resolved".equals(status);
        if (complaintId == null || complaintId.trim().isEmpty() || !validStatus) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (ComplaintDAO.updateStatus(complaintId, status)) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
