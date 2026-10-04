package com.cms.servlet;

import com.cms.util.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/analytics")
public class AnalyticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Manually building JSON to avoid external dependencies
        StringBuilder json = new StringBuilder("{");

        try (Connection con = DBConnection.getConnection()) {

            // 1. Get Status Counts
            json.append("\"status\": {");
            String statusQuery = "SELECT status, COUNT(complaint_id) as count FROM complaints GROUP BY status";
            try (PreparedStatement pst = con.prepareStatement(statusQuery);
                    ResultSet rs = pst.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        json.append(",");
                    json.append("\"").append(rs.getString("status")).append("\":").append(rs.getInt("count"));
                    first = false;
                }
            }
            json.append("},");

            // 2. Get Category Counts
            json.append("\"category\": {");
            String categoryQuery = "SELECT category, COUNT(complaint_id) as count FROM complaints GROUP BY category";
            try (PreparedStatement pst = con.prepareStatement(categoryQuery);
                    ResultSet rs = pst.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        json.append(",");
                    json.append("\"").append(rs.getString("category")).append("\":").append(rs.getInt("count"));
                    first = false;
                }
            }
            json.append("}");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error\"}");
            return;
        }

        json.append("}");
        response.getWriter().write(json.toString());
    }
}