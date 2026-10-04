package com.cms.servlet;

import com.cms.dao.UserDAO;
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

@WebServlet("/api/get-staff")
public class GetStaffServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");

        // Security check: Only Admin can fetch the staff list for assignment
        if (user == null || !"Admin".equals(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("[]");
            return;
        }

        // Retrieves all users with the role 'Staff'
        List<User> staffList = UserDAO.getStaff();

        // Build JSON array manually without needing external libraries like Gson
        StringBuilder jsonBuilder = new StringBuilder("[");
        if (staffList != null) {
            for (int i = 0; i < staffList.size(); i++) {
                User u = staffList.get(i);
                jsonBuilder.append("{\"id\":").append(u.getId())
                        .append(",\"fullName\":\"").append(escapeJson(u.getFullName())).append("\"}");
                if (i < staffList.size() - 1) {
                    jsonBuilder.append(",");
                }
            }
        }
        jsonBuilder.append("]");

        out.write(jsonBuilder.toString());
        out.flush();
    }

    private String escapeJson(String str) {
        if (str == null)
            return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}