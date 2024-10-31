package MCQModule;

import java.sql.*;
import java.util.*;

public class MCQModule {

    static final String DB_URL = "jdbc:mysql://localhost:3306/online_examination_system";
    static final String USER = "root";
    static final String PASS = "2004";

    static final int totalQuestions = 20;
    static final long timeLimit = 6000; // 1 minute in milliseconds

    static class Question {
        int questionNo;
        String questionText;
        String[] options = new String[4];
        String correctAnswer;

        Question(int no, String text, String[] opts, String correct) {
            this.questionNo = no;
            this.questionText = text;
            System.arraycopy(opts, 0, this.options, 0, 4);
            this.correctAnswer = correct;
        }
    }

    public static void callMCQModule() {
        List<Question> questions = fetchRandomQuestions(totalQuestions);
        Map<Integer, String> userAnswers = new HashMap<>();
        List<Question> skippedQuestions = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);

        long startTime = System.currentTimeMillis();

        System.out.println("\n--- Attempt the Questions ---");
        for (int i = 0; i < questions.size(); i++) {
            if (System.currentTimeMillis() - startTime >= timeLimit) {
                System.out.println("\nTime is up!");
                break;
            }

            askQuestion(i + 1, questions.get(i));
            System.out.println("e) Skip for now");

            System.out.print("Your answer: ");
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("e")) {
                skippedQuestions.add(questions.get(i));
            } else {
                userAnswers.put(questions.get(i).questionNo, answer);
            }
        }

        if (System.currentTimeMillis() - startTime < timeLimit && !skippedQuestions.isEmpty()) {
            System.out.println("\n--- Answer the Skipped Questions ---");
            for (int i = 0; i < skippedQuestions.size(); i++) {
                if (System.currentTimeMillis() - startTime >= timeLimit) {
                    System.out.println("\nTime is up!");
                    break;
                }

                askQuestion(i + 1, skippedQuestions.get(i));

                System.out.print("Your answer: ");
                String answer = scanner.nextLine().trim();
                userAnswers.put(skippedQuestions.get(i).questionNo, answer);
            }
        }

        if (System.currentTimeMillis() - startTime < timeLimit) {
            editAnswers(questions, userAnswers, scanner);
        } else {
            System.out.println("\nTime is up! No more edits allowed.");
        }

        int score = calculateScore(questions, userAnswers);
        System.out.println("\nYour Total Score: " + score + "/" + totalQuestions);

        scanner.close();
    }

    private static List<Question> fetchRandomQuestions(int limit) {
        List<Question> questions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {

            String sql = "SELECT * FROM java_mcq ORDER BY RAND() LIMIT " + limit;
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int no = rs.getInt("question_no");
                String text = rs.getString("question_text");
                String[] options = {
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d")
                };
                String correct = rs.getString("correct_answer");
                questions.add(new Question(no, text, options, correct));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    private static void askQuestion(int displayNo, Question q) {
        System.out.println("\nQ" + displayNo + ": " + q.questionText);
        System.out.println("a) " + q.options[0]);
        System.out.println("b) " + q.options[1]);
        System.out.println("c) " + q.options[2]);
        System.out.println("d) " + q.options[3]);
    }

    private static void editAnswers(List<Question> questions, Map<Integer, String> userAnswers, Scanner scanner) {
        System.out.println("\nDo you want to edit any answers? (yes/no)");
        String choice = scanner.nextLine();

        while (choice.equalsIgnoreCase("yes")) {
            System.out.println("\nHere are the questions you answered:");
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                System.out.println("Q" + (i + 1) + ": " + q.questionText +
                        " [Your Answer: " + userAnswers.getOrDefault(q.questionNo, "Not Answered") + "]");
            }

            System.out.print("Enter the question number you want to edit: ");
            int qIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            if (qIndex >= 0 && qIndex < questions.size()) {
                Question questionToEdit = questions.get(qIndex);
                askQuestion(qIndex + 1, questionToEdit);

                System.out.print("Enter your new answer: ");
                String newAnswer = scanner.nextLine().trim();
                userAnswers.put(questionToEdit.questionNo, newAnswer);
            } else {
                System.out.println("Invalid question number. Please try again.");
            }

            System.out.print("Do you want to edit another answer? (yes/no): ");
            choice = scanner.nextLine();
        }
    }

    private static int calculateScore(List<Question> questions, Map<Integer, String> userAnswers) {
        int score = 0;
        for (Question q : questions) {
            String correctAnswer = q.correctAnswer.trim();
            String userAnswer = userAnswers.getOrDefault(q.questionNo, "").trim();
            if (correctAnswer.equalsIgnoreCase(userAnswer)) {
                score++;
            }
        }
        return score;
    }
}
