package com.cms.servlet;

import com.cms.model.User;
import com.cms.dao.UserDAO;
import com.cms.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

// Returns the list of staff members. Only the Admin may call it.
@WebServlet("/api/staff")
public class StaffListServlet extends HttpServlet {

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
        if (!"Admin".equals(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        List<User> staff = UserDAO.getStaff();
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < staff.size(); i++) {
            User s = staff.get(i);
            json.append("{\"id\":").append(s.getId())
                .append(",\"name\":\"").append(JsonUtil.escape(s.getFullName())).append("\"}");
            if (i < staff.size() - 1) json.append(",");
        }
        json.append("]");

        response.getWriter().write(json.toString());
    }
}
