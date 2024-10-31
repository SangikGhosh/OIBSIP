package authentication;

import java.sql.*;
import java.util.*;
import OTPService.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Authentication {
    private static final String url = "jdbc:mysql://localhost:3306/online_examination_system";
    private static final String dbusername = "root";
    private static final String dbpassword = "2004";

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    public boolean authenticateUserReservationSystem(int choice,String username,String email,String password) {
        if(!isValidEmail(email)) {
            System.out.println("Please maintain correct email format...");
            System.exit(0);
        }

        if (choice == 1) {
            // Login
            if (loginRS(username, email, password)) {
                if(OTPService.checkOTPRS(email)) {
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
            if (signUpRS(username, password, email)) {
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

    private static boolean loginRS(String username, String email, String password) {
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

    private static boolean signUpRS(String username, String password, String email) {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        if(OTPService.checkOTPRS(email)) {
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

    public boolean authenticateUserOnlineExamination(int choice,String username,String email,String password) {
        if(!isValidEmail(email)) {
            System.out.println("Please maintain correct email format...");
            System.exit(0);
        }

        if (choice == 1) {
            // Login
            if (loginOE(username, email, password)) {
                if(OTPService.checkOTPOE(email)) {
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
            if (signUpOE(username, password, email)) {
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

    private static boolean loginOE(String username, String email, String password) {
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

    private static boolean signUpOE(String username, String password, String email) {
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        if(OTPService.checkOTPOE(email)) {
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

    public boolean verifyEmailAndUpdate(String email) {
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return false;
        }
        System.out.println(email);
        if (!emailExists(email)) {
            System.out.println("Email does not exist.");
            return false;
        }

        if (OTPService.checkOTPOE(email)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("OTP verified. Choose an option:");
            System.out.println("1. Change Username");
            System.out.println("2. Change Password");
            System.out.println("3. Change Username and Password both");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();
            String newUsername;
            String newPassword;

            switch (choice) {
                case 1:
                    System.out.print("Enter new username: ");
                    newUsername = scanner.nextLine();
                    return updateUsername(email, newUsername);
                case 2:
                    System.out.print("Enter new password: ");
                    newPassword = scanner.nextLine();
                    return updatePassword(email, newPassword);
                case 3:
                    System.out.print("Enter new username: ");
                    newUsername = scanner.nextLine();
                    System.out.print("Enter new password: ");
                    newPassword = scanner.nextLine();
                    return updateUsernameAndPassword(email, newUsername, newPassword);
                case 4:
                    System.out.println("Thanks for using Online Examination System. Goodbye.\n");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice.");
                    return false;
            }
        } else {
            System.out.println("Wrong OTP entered.");
            return false;
        }
    }

    private static boolean emailExists(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(url, dbusername, dbpassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            return false;
        }
    }

    private static boolean updateUsername(String email, String newUsername) {
        String query = "UPDATE users SET username = ? WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(url, dbusername, dbpassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newUsername);
            preparedStatement.setString(2, email);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            return false;
        }
    }

    private static boolean updatePassword(String email, String newPassword) {
        String query = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(url, dbusername, dbpassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, email);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            return false;
        }
    }

    private static boolean updateUsernameAndPassword(String email, String newUsername, String newPassword) {
        String query = "UPDATE users SET username = ?, password = ? WHERE email = ?";
        try (Connection connection = DriverManager.getConnection(url, dbusername, dbpassword);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newUsername);
            preparedStatement.setString(2, newPassword);
            preparedStatement.setString(3, email);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            return false;
        }
    }
}
