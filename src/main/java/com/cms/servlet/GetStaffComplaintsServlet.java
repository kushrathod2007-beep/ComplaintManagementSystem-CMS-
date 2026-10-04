package com.cms.servlet;

import com.cms.dao.ComplaintDAO;
import com.cms.model.Complaint;
import com.cms.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/get-staff-complaints")
public class GetStaffComplaintsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null || !"Staff".equals(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("[]");
            return;
        }

        // Fetch complaints assigned specifically to this staff member's ID
        List<Complaint> complaints = ComplaintDAO.getComplaintsByStaff(user.getId());

        // Manual JSON serialization to avoid external dependencies
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < complaints.size(); i++) {
            Complaint c = complaints.get(i);
            sb.append("{")
                    .append("\"complaintId\":\"").append(c.getComplaintId()).append("\",")
                    .append("\"title\":\"").append(escape(c.getTitle())).append("\",")
                    .append("\"category\":\"").append(escape(c.getCategory())).append("\",")
                    .append("\"description\":\"").append(escape(c.getDescription())).append("\",")
                    .append("\"status\":\"").append(escape(c.getStatus())).append("\"")
                    .append("}");
            if (i < complaints.size() - 1)
                sb.append(",");
        }
        sb.append("]");

        out.write(sb.toString());
        out.flush();
    }

    private String escape(String str) {
        if (str == null)
            return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}