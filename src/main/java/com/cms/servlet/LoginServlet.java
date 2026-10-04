package com.cms.servlet;

import com.cms.model.User;
import com.cms.dao.UserDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String usernameOrEmail = request.getParameter("username");
        String password = request.getParameter("password");

        User user = UserDAO.validateUser(usernameOrEmail, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", user);
            session.setAttribute("currentUser", user);
            session.setAttribute("role", user.getRole());

            String redirectPage = "dashboard.html"; // Default user dashboard
            String role = user.getRole();

            if ("Admin".equalsIgnoreCase(role)) {
                redirectPage = "admin.html";
            } else if ("Staff".equalsIgnoreCase(role)) {
                redirectPage = "staff.html";
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(
                "{\"success\": true, \"role\": \"" + role + "\", \"redirect\": \"" + redirectPage + "\", \"message\": \"Login Successful\"}"
            );
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid username or password.\"}");
        }
    }
}