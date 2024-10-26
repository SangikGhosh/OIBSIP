package authentication;

import java.sql.*;
import java.util.*;
import OTPService.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Authentication {
    private static final String url = "jdbc:mysql://localhost:3306/online_reservation_system";
    private static final String dbusername = "root";
    private static final String dbpassword = "2004";

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
    public boolean authenticateUser(int choice,String username,String email,String password) {
        if(!isValidEmail(email)) {
            System.out.println("Please maintain correct email format...");
            System.exit(0);
        }

        if (choice == 1) {
            // Login
            if (login(username, email, password)) {
                if(OTPService.checkOTP(email)) {
                    System.out.println("Login successfully.\n\n");
                    return true;
                } else {
                    System.out.println("Login failed. Wrong OTP entered...");
                    return false;
                }
            } else {
                System.out.println("Invalid username, email, or password.");
                return false;
            }
        } else if (choice == 2) {
            // Sign Up
            if (signUp(username, password, email)) {
                System.out.println("Sign up successfully.\n\n");
                return true;
            } else {
                System.out.println("Sign up failed. Username or email may already exist.");
                return false;
            }
        } else {
            System.out.println("Invalid choice.");
            return false;
        }
    }

    private static boolean login(String username, String email, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND email = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(url, dbusername, dbpassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Returns true if a record was found
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            return false;
        }
    }

    private static boolean signUp(String username, String password, String email) {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        if(OTPService.checkOTP(email)) {
            try (Connection connection = DriverManager.getConnection(url, dbusername, dbpassword);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setString(3, email);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0; // Returns true if the insert was successful
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage());
                return false;
            }
        } else {
            System.out.println("Wrong OTP entered.");
            return false;
        }
    }

}
