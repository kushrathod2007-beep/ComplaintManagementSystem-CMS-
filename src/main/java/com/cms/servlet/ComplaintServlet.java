package com.cms.servlet;

import com.cms.model.Complaint;
import com.cms.model.User;
import com.cms.util.ComplaintIdGenerator;
import com.cms.dao.ComplaintDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/complaint")
public class ComplaintServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Must be set before reading any parameter, so non-English text is stored correctly
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"Unauthorized. Please login.\"}");
            return;
        }

        String title = request.getParameter("title");
        String category = request.getParameter("category");
        String description = request.getParameter("description");

        if (title == null || title.trim().isEmpty()
                || description == null || description.trim().isEmpty()
                || category == null || category.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"Please fill in the title, category and description.\"}");
            return;
        }

        title = title.trim();
        category = category.trim();
        description = description.trim();

        if (title.length() > 150 || category.length() > 50) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"message\": \"Title is too long (max 150 characters).\"}");
            return;
        }

        // A random ID can very rarely repeat; if the insert fails, try again with a fresh ID
        boolean saved = false;
        String complaintId = null;
        for (int attempt = 0; attempt < 3 && !saved; attempt++) {
            complaintId = ComplaintIdGenerator.generateId();
            Complaint complaint = new Complaint(complaintId, user.getId(), title, description, category);
            saved = ComplaintDAO.createComplaint(complaint);
        }

        if (saved) {
            response.getWriter().write("{\"success\": true, \"complaintId\": \"" + complaintId + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"message\": \"Failed to register complaint.\"}");
        }
    }
}
