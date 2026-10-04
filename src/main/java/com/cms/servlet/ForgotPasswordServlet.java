package com.cms.servlet;

import com.cms.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

@WebServlet("/api/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession();

        // Read JSON body cleanly
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String body = sb.toString();

        // Simple check to identify action type from the payload
        if (body.contains("\"action\":\"send-otp\"")) {
            String email = extractValue(body, "email");
            if (email != null && !email.isEmpty()) {
                String otp = String.format("%06d", new Random().nextInt(999999));

                session.setAttribute("RESET_EMAIL", email);
                session.setAttribute("RESET_OTP", otp);

                System.out.println("==========================================");
                System.out.println(">>> PASSWORD RESET OTP FOR " + email + ": " + otp);
                System.out.println("==========================================");

                out.write("{\"success\": true, \"message\": \"OTP sent! Check your server console.\"}");
            } else {
                out.write("{\"success\": false, \"message\": \"Please provide a valid email.\"}");
            }
        } else if (body.contains("\"action\":\"verify-reset\"")) {
            String enteredOtp = extractValue(body, "otp");
            String newPassword = extractValue(body, "newPassword");

            String sessionOtp = (String) session.getAttribute("RESET_OTP");
            String sessionEmail = (String) session.getAttribute("RESET_EMAIL");

            if (sessionOtp != null && sessionOtp.equals(enteredOtp) && sessionEmail != null) {
                boolean updated = UserDAO.updatePasswordByEmail(sessionEmail, newPassword);
                if (updated) {
                    session.removeAttribute("RESET_OTP");
                    session.removeAttribute("RESET_EMAIL");
                    out.write("{\"success\": true, \"message\": \"Password updated successfully!\"}");
                } else {
                    out.write("{\"success\": false, \"message\": \"Database update failed.\"}");
                }
            } else {
                out.write("{\"success\": false, \"message\": \"Invalid or expired OTP code.\"}");
            }
        } else {
            out.write("{\"success\": false, \"message\": \"Invalid request action.\"}");
        }
        out.flush();
    }

    private String extractValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":\"";
            int startIndex = json.indexOf(searchKey);
            if (startIndex == -1) {
                searchKey = "\"" + key + "\":";
                startIndex = json.indexOf(searchKey);
                if (startIndex == -1)
                    return "";
                startIndex += searchKey.length();
                int endIndex = json.indexOf(",", startIndex);
                if (endIndex == -1)
                    endIndex = json.indexOf("}", startIndex);
                return json.substring(startIndex, endIndex).replaceAll("[\"}]", "").trim();
            }
            startIndex += searchKey.length();
            int endIndex = json.indexOf("\"", startIndex);
            return json.substring(startIndex, endIndex).trim();
        } catch (Exception e) {
            return "";
        }
    }
}