import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    private static final int MIN_RANGE = 1;
    private static final int MAX_RANGE = 100;
    private static final int MAX_ATTEMPTS = 10;
    private static final int MAX_ROUNDS = 3;

    public static void main(String[] args) {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);
        int playerScore = 0;
        int computerScore = 0;

        System.out.println("NUMBER GUESSING GAME");
        System.out.println("Total Number Of Rounds : " + MAX_ROUNDS);
        System.out.println("Attempts To Guess Number In Each Round : " + MAX_ATTEMPTS + "\n");

        for (int i = 1; i <= MAX_ROUNDS; i++) {
            int number = random.nextInt(MAX_RANGE) + MIN_RANGE;
            int attempts = 0;
            boolean guessedCorrectly = false;

            System.out.printf("Round %d: Guess the number between %d and %d in %d attempts.\n",
                    i, MIN_RANGE, MAX_RANGE, MAX_ATTEMPTS);

            while (attempts < MAX_ATTEMPTS) {
                System.out.print("Enter your guess: ");
                int guessNumber = scanner.nextInt();
                attempts++;

                if (guessNumber == number) {
                    int score = MAX_ATTEMPTS - attempts;
                    playerScore += score;
                    System.out.printf("Hurray! You guessed the number! Attempts = %d. Round Score = %d\n",
                            attempts, score);
                    guessedCorrectly = true;
                    break;
                } else if (guessNumber < number) {
                    System.out.printf("The number is greater than %d. Attempts Left = %d.\n",
                            guessNumber, MAX_ATTEMPTS - attempts);
                } else {
                    System.out.printf("The number is less than %d. Attempts Left = %d.\n",
                            guessNumber, MAX_ATTEMPTS - attempts);
                }
            }

            if (!guessedCorrectly) {
                int score = MAX_ATTEMPTS;
                computerScore += score;
                System.out.printf("Oops! You ran out of attempts. The correct number was %d.\n", number);
                System.out.printf("Computer wins this round with a score of %d!\n\n", score);
            }
        }

        System.out.printf("Game Over! Final Scores:\nPlayer: %d\nComputer: %d\n", playerScore, computerScore);
        if (playerScore > computerScore) {
            System.out.println("Congratulations! You win the game!");
        } else if (playerScore < computerScore) {
            System.out.println("Computer wins the game! Better luck next time!");
        } else {
            System.out.println("It's a tie!");
        }

        scanner.close();
    }
}
