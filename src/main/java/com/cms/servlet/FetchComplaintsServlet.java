package com.cms.servlet;

import com.cms.model.Complaint;
import com.cms.model.User;
import com.cms.dao.ComplaintDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/get-complaints")
public class FetchComplaintsServlet extends HttpServlet {

    private static String esc(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char ch : s.toCharArray()) {
            switch (ch) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (ch < 0x20) sb.append(String.format("\\u%04x", (int) ch));
                    else sb.append(ch);
            }
        }
        return sb.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Fetch all complaints to ensure visibility across all dashboards
        List<Complaint> complaints = ComplaintDAO.getAllComplaints();

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < complaints.size(); i++) {
            Complaint c = complaints.get(i);
            String cid = esc(c.getComplaintId());
            json.append("{")
                .append("\"id\":\"").append(cid).append("\",")
                .append("\"complaintId\":\"").append(cid).append("\",")
                .append("\"title\":\"").append(esc(c.getTitle())).append("\",")
                .append("\"category\":\"").append(esc(c.getCategory())).append("\",")
                .append("\"status\":\"").append(esc(c.getStatus()))
                .append("\"}");
            if (i < complaints.size() - 1) json.append(",");
        }
        json.append("]");

        response.getWriter().write(json.toString());
    }
}