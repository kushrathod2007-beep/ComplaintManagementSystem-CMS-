package com.cms.dao;

import com.cms.model.User;
import com.cms.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Passwords are stored as BCrypt hashes, never as plain text[cite: 3].
    public static boolean registerUser(User user) {
        String query = "INSERT INTO users (full_name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, user.getFullName());
            pst.setString(2, user.getEmail());
            pst.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
            pst.setString(4, user.getRole());

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User validateUser(String email, String password) {
        if (email == null || password == null) {
            return null;
        }

        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection()) {

            User user = null;
            boolean legacyPlainText = false;

            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, email);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String stored = rs.getString("password");
                        boolean ok;

                        if (stored != null && stored.startsWith("$2")) {
                            // Normal case: stored value is a BCrypt hash[cite: 3]
                            ok = BCrypt.checkpw(password, stored);
                        } else {
                            // Old account created before hashing was added: compare once, then upgrade below[cite: 3]
                            legacyPlainText = true;
                            ok = stored != null && MessageDigest.isEqual(
                                    stored.getBytes(StandardCharsets.UTF_8),
                                    password.getBytes(StandardCharsets.UTF_8));
                        }

                        if (ok) {
                            user = new User();
                            user.setId(rs.getInt("id"));
                            user.setFullName(rs.getString("full_name"));
                            user.setEmail(rs.getString("email"));
                            user.setRole(rs.getString("role"));
                        }
                    }
                }
            }

            // Replace the old plain-text password with a hash after a successful login[cite: 3]
            if (user != null && legacyPlainText) {
                upgradePassword(con, user.getId(), password);
            }
            return user;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void upgradePassword(Connection con, int userId, String plainPassword) {
        String query = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, BCrypt.hashpw(plainPassword, BCrypt.gensalt(10)));
            pst.setInt(2, userId);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update password via email for the Forgot Password / OTP feature
    public static boolean updatePasswordByEmail(String email, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            
            // Hash the new password securely using BCrypt just like registration and upgrades
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(10));
            pst.setString(1, hashedPassword);
            pst.setString(2, email);
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Checks if a specific user ID belongs to a Staff member[cite: 3]
    public static boolean isStaff(Integer userId) {
        if (userId == null) return false;
        String query = "SELECT role FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return "Staff".equals(rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Retrieves a list of all Staff members to populate the Admin dropdown[cite: 3]
    public static List<User> getStaff() {
        List<User> staffList = new ArrayList<>();
        String query = "SELECT id, full_name, email, role FROM users WHERE role = 'Staff'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                staffList.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffList;
    }
}