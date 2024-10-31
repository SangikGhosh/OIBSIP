import java.sql.*;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import authentication.*;

public class OnlineReservationSystem {
    private static String url = "jdbc:mysql://localhost:3306/online_reservation_system";
    private static String dbusername = "root";
    private static String dbpassword = "2004";

    public static class PnrRecord {
        private String pnrNumber;
        private String passengerName;
        private String trainNumber;
        private String classType;
        private String journeyDate;
        private String from;
        private String to;

        Scanner sc = new Scanner(System.in);

        public String getPnrNumber() {
            Random random = new Random();
            StringBuilder sb = new StringBuilder(10);
            for (int i = 0; i < 10; i++) {
                if (random.nextBoolean()) {
                    sb.append((char) ('A' + random.nextInt(26)));
                } else {
                    sb.append(random.nextInt(10));
                }
            }
            pnrNumber = sb.toString();
            return pnrNumber;
        }

        public String getPassengerName() {
            System.out.print("Enter the passenger name: ");
            passengerName = sc.nextLine();
            return passengerName;
        }

        public String getTrainNumber() {
            System.out.print("Enter the train number: ");
            trainNumber = sc.nextLine();
            return trainNumber;
        }

        public String getClassType() {
            System.out.print("Enter the class type: ");
            classType = sc.nextLine();
            return classType;
        }

        public String getJourneyDate() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false); // Strict date parsing
            while (true) {
                System.out.print("Enter the Journey date as 'YYYY-MM-DD' format: ");
                journeyDate = sc.nextLine();
                try {
                    Date date = sdf.parse(journeyDate);
                    Date currentDate = new Date();
                    if (date.before(currentDate)) {
                        System.out.println("The journey date must be in the future. Please try again.");
                    } else {
                        return journeyDate;
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please enter the date in 'YYYY-MM-DD' format.");
                }
            }
        }

        public String getFrom() {
            System.out.print("Enter the starting place: ");
            from = sc.nextLine();
            return from;
        }

        public String getTo() {
            System.out.print("Enter the destination place: ");
            to = sc.nextLine();
            return to;
        }
    }

    public static void main(String[] args) {
        Authentication auth = new Authentication();
        Scanner sc = new Scanner(System.in);
        System.out.println("1. Existing User (Login)");
        System.out.println("2. New User (Sign Up)");
        System.out.print("Please select an option (1 or 2): ");
        int chs = sc.nextInt();
        sc.nextLine();
        if (chs != 1 && chs != 2) {
            System.out.println("Invalid choice");
            System.exit(130);
        }

        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.next().trim();
        System.out.print("Enter Password: ");
        String password = sc.next().trim();

        if (auth.authenticateUserReservationSystem(chs, username, email, password)) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (Connection connection = DriverManager.getConnection(url, dbusername, dbpassword)) {
                    while (true) {
                        String insertQuery = "INSERT INTO reservations (email, pnr_number, passenger_name, train_number, class_type, journey_date, from_location, to_location) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        String deleteQuery = "DELETE FROM reservations WHERE pnr_number = ? AND email = ?";
                        String showQuery = "SELECT * FROM reservations WHERE email = ?";

                        System.out.println("Welcome " + username);
                        System.out.println("Enter the choice: ");
                        System.out.println("1. Book a ticket");
                        System.out.println("2. Cancel a ticket");
                        System.out.println("3. Show All Records");
                        System.out.println("4. Exit");
                        System.out.print("Enter your choice: ");
                        int choice = sc.nextInt();

                        if (choice == 1) {
                            PnrRecord p1 = new PnrRecord();
                            String pnrNumber = p1.getPnrNumber();
                            String passengerName = p1.getPassengerName();
                            String trainNumber = p1.getTrainNumber();
                            String classType = p1.getClassType();
                            String journeyDate = p1.getJourneyDate();
                            String fromLocation = p1.getFrom();
                            String toLocation = p1.getTo();


                            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                                preparedStatement.setString(1, email);
                                preparedStatement.setString(2, pnrNumber);
                                preparedStatement.setString(3, passengerName);
                                preparedStatement.setString(4, trainNumber);
                                preparedStatement.setString(5, classType);
                                preparedStatement.setString(6, journeyDate);
                                preparedStatement.setString(7, fromLocation);
                                preparedStatement.setString(8, toLocation);

                                int rowsAffected = preparedStatement.executeUpdate();
                                System.out.println(rowsAffected > 0 ? "Record added successfully." : "No records were added.");
                                System.out.println("PNR number is " + pnrNumber + "\n");
                            } catch (SQLException e) {
                                System.err.println("SQLException: " + e.getMessage());
                            }

                        } else if (choice == 2) {
                            System.out.print("Enter the PNR number to delete the record: ");
                            String pnrNumber = sc.next();
                            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                                preparedStatement.setString(1, pnrNumber);
                                preparedStatement.setString(2, email);
                                int rowsAffected = preparedStatement.executeUpdate();
                                System.out.println(rowsAffected > 0 ? "Record deleted successfully." : "No record found for PNR " + pnrNumber + " associated with email " + email);
                            } catch (SQLException e) {
                                System.err.println("SQLException: " + e.getMessage());
                            }

                        } else if (choice == 3) {
                            try (PreparedStatement preparedStatement = connection.prepareStatement(showQuery)) {
                                preparedStatement.setString(1, email);
                                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                                    boolean hasRecords = false;
                                    System.out.println("\nRecords associated with email: " + email + "\n");
                                    while (resultSet.next()) {
                                        hasRecords = true;
                                        System.out.println("Email: " + resultSet.getString("email"));
                                        System.out.println("PNR Number: " + resultSet.getString("pnr_number"));
                                        System.out.println("Passenger Name: " + resultSet.getString("passenger_name"));
                                        System.out.println("Train Number: " + resultSet.getString("train_number"));
                                        System.out.println("Class Type: " + resultSet.getString("class_type"));
                                        System.out.println("Journey Date: " + resultSet.getString("journey_date"));
                                        System.out.println("From Location: " + resultSet.getString("from_location"));
                                        System.out.println("To Location: " + resultSet.getString("to_location"));
                                        System.out.println("--------------------------------\n");
                                    }
                                    if (!hasRecords) {
                                        System.out.println("No records found for email: " + email+ "\n");
                                    }
                                }
                            } catch (SQLException e) {
                                System.err.println("SQLException: " + e.getMessage());
                            }

                        } else if (choice == 4) {
                            System.out.println("Exiting the program.\n");
                            break;
                        } else {
                            System.out.println("Invalid Choice Entered.\n");
                        }
                    }

                } catch (SQLException e) {
                    System.out.println("Connection not established.");
                    System.err.println("SQLException: " + e.getMessage());
                    System.exit(0);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Error loading JDBC driver: " + e.getMessage());
            }
        } else {
            System.exit(0);
        }

        sc.close();
    }
}
