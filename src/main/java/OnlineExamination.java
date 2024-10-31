import MCQModule.*;

import authentication.*;

import java.util.Scanner;

public class OnlineExamination {
    private static String url = "jdbc:mysql://localhost:3306/online_examination_system";
    private static String dbusername = "root";
    private static String dbpassword = "2004";

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    private String username;
    private String email;
    private String password;
    public static void main(String[] args) {
        Authentication auth = new Authentication();
        OnlineExamination on = new OnlineExamination();
        Scanner sc = new Scanner(System.in);
        System.out.println("1. Existing User (Login)");
        System.out.println("2. New User (Sign Up)");
        System.out.println("3. Update profile(if forgot password) ");
        System.out.print("Please select an option: ");
        int chs = sc.nextInt();
        sc.nextLine();

        if(chs == 3) {
            // profile update
            System.out.print("Enter Email: ");
            on.email = sc.nextLine();
            if (auth.verifyEmailAndUpdate(on.email)) {
                System.out.println("Profile update successfully.");
                System.exit(0);
            } else {
                System.out.println("Profile update failed.");
            }
        } else if ( chs == 1 || chs == 2) {
            System.out.print("Enter Username: ");
            on.username = sc.nextLine();
            System.out.print("Enter Email: ");
            on.email = sc.nextLine().trim();
            System.out.print("Enter Password: ");
            on.password = sc.next().trim();
            if (auth.authenticateUserOnlineExamination(chs, on.getUsername(), on.getEmail(), on.getPassword())) {
                System.out.println("\n\nChoose the domain in which you want to perform the examination:\n1. Java Development\n2. Web Development\n3. Exit");
                chs = sc.nextInt();
                sc.nextLine();
                if(chs == 1) {
                    MCQModule.callMCQModule();
                } else if(chs == 2) {
                    System.out.println("This domain will coming soon...");
                } else {
                    System.out.println("Thanks for using Online Examination System.Have a nice day, goodbye...\n");
                }
            }
        } else {
            System.out.println("Invalid choice");
            System.exit(130);
        }
    }
}
