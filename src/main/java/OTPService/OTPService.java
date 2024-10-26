package OTPService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

public class OTPService {
    static String OTP;
    public static String genOTP () {
        Random random = new Random();
        OTP = String.format("%04d", random.nextInt(10000));
        return OTP;
    }
    public static void sendOTP(String email, String genOTP) {
        String to = email;
        String from = "sangik.ghosh1@gmail.com";
        String host = "smtp.gmail.com";
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "ljgdhcmzacynaofn");
            }

        });
        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Message from File Hider Service");
            message.setText("Your One time Password for File Hider Service is " + genOTP);

            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    public static boolean checkOTP(String email) {
        String OTP = OTPService.genOTP();
        OTPService.sendOTP(email ,OTP);
        System.out.println("Enter the OTP receive on mail: ");
        Scanner sc = new Scanner(System.in);
        String enOTP = sc.nextLine();
        if(OTP.equals(enOTP)) {
            return true;
        } else {
            return false;
        }
    }

}
