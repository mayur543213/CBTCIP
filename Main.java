
//Mayur Arde
//CipherByteTechnology

import java.util.Scanner;
import java.util.Random;

public class Main {
    private static final int MIN_RANGE = 1;
    private static final int MAX_RANGE = 100;
    private static final int MAX_ATTEMPTS = 10;
    private static final int MAX_ROUNDS = 3;

    public static void main(String[] args) {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);
        int totalScore = 0;

        System.out.println("<<<<<<!!!!THE GAME OF GUESSING NUMBER!!!!>>>>>>");
        System.out.printf("Total Number of Rounds: %d\n", MAX_ROUNDS);
        System.out.printf("Attempts to Guess Number In Each Round: %d\n\n", MAX_ATTEMPTS);

        for (int round = 1; round <= MAX_ROUNDS; round++) {
            int number = random.nextInt(MAX_RANGE) + MIN_RANGE;
            int attempts = 0;

            System.out.printf("Round %d: Guess the number between %d and %d in %d attempts.\n", round, MIN_RANGE, MAX_RANGE, MAX_ATTEMPTS);

            while (attempts < MAX_ATTEMPTS) {
                System.out.print("Enter Your Guess: ");
                int guessNumber = scanner.nextInt();
                attempts++;

                if (guessNumber == number) {
                    int score = MAX_ATTEMPTS - attempts;
                    totalScore += score;
                    System.out.printf("Congratulations! Number Guessed Successfully in %d attempts. Round Score: %d\n\n", attempts, score);
                    break;
                } else if (guessNumber < number) {
                    System.out.printf("The number is greater than %d. Attempts left: %d.\n", guessNumber, MAX_ATTEMPTS - attempts);
                } else {
                    System.out.printf("The number is less than %d. Attempts left: %d.\n", guessNumber, MAX_ATTEMPTS - attempts);
                }
            }

            if (attempts == MAX_ATTEMPTS) {
                System.out.printf("\nRound %d\n", round);
                System.out.println("Attempts exhausted!");
                System.out.printf("The correct number was: %d\n\n", number);
            }
        }

        System.out.printf("Game Over! Total Score: %d\n", totalScore);
        scanner.close();
    }
}
