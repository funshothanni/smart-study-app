package ca.umanitoba.cs.thannio.ui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputPrompts {
    private InputPrompts() {
    }

    /**
     * this method is used to continuously prompt the user for a POSITIVE integer and throws an exception
     * if unexpected input is entered
     *
     * @param sc    scanner to get number from user
     * @param label instruction to user on number we are getting e.g. "enter memberID"
     * @return value the integer converted from the String entered by the user
     */
    public static int promptPositiveInt(Scanner sc, String label) {
        int value = 0;
        boolean valid = false;

        while (!valid) {
            try {
                System.out.print(label + ": ");
                value = Integer.parseInt(sc.nextLine().trim());

                if (value >= 1) {
                    valid = true;
                } else {
                    System.out.println("Please enter a positive integer");
                }
            } catch (NumberFormatException e) {
                System.err.println("Please enter a valid input, a number");
            }
        }
        return value;
    }

    /**
     * this method is used to continuously prompt the user for a non-blank String
     * converts the inputted String to upper case
     *
     * @param sc    scanner to get string from user
     * @param label instruction to user on String we are getting e.g. "enter memberID"
     * @return s a non blank FULL CAPS String
     */
    public static String promptNonBlankUpperString(Scanner sc, String label) {
        String s = "";
        boolean ok = false;

        while (!ok) {
            System.out.print(label + ": ");
            s = sc.nextLine().trim().toUpperCase();

            if (!s.isBlank()) {
                ok = true;
            } else {
                System.out.println(label + " cannot be blank! Re-enter " + label);
            }
        }
        return s;
    }

    /**
     * this method is used to continuously prompt the user for valid rating (1-5)
     *
     * @param sc scanner to get int from the user
     * @return rating, a valid number x, such that (1 <= x <= 5)
     */
    public static int promptRating(Scanner sc) {
        int rating = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print("Enter Review Rating (1-5)" + ": ");
            String input = sc.nextLine().trim();

            try {
                rating = Integer.parseInt(input);
                if (rating >= 1 && rating <= 5) {
                    valid = true;
                } else {
                    System.out.println("Invalid Rating! Please enter a number between 1 and 5.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid input! Please enter numbers only.");
            }
        }
        return rating;
    }

    /**
     * this method is used to continuously prompt the user for a valid date in the format "YYYY-MM-DD"
     *
     * @param sc the scanner used to get input from the user
     * @return the valid date gotten from the user
     */
    public static LocalDate promptBookingDate(Scanner sc) {
        LocalDate date = null;
        boolean valid = false;
        while (!valid) {
            System.out.print("Enter a date in the format 'YYYY-MM-DD' : ");
            String input = sc.nextLine().trim();

            try {
                date = LocalDate.parse(input);
                valid = true;
            } catch (DateTimeParseException e) {
                System.err.println("Invalid Date Format! Please enter a valid date. Don't forget to add the hyphens '-'. ");
            }
        }
        return date;
    }

    /**
     * this method is used to continuously prompt the user for a valid time in the format "0X:00" or "XX:00"
     *
     * @param sc the scanner used to get input from the user
     * @return the valid time gotten from the user
     */
    public static LocalTime promptBookingTime(Scanner sc) {
        LocalTime time = null;
        boolean valid = false;
        while (!valid) {
            System.out.println("Enter Preferred Booking Start Time (HH:MM must be format 'XX:00)' " +
                    "with a 0 padded single digit hours e.g. '(09:00)' not '(9:00)': ");
            String input = sc.nextLine().trim();

            try {
                time = LocalTime.parse(input);
                int minutes = time.getMinute();
                if (minutes != 0) {
                    System.out.println("Invalid Time Format! Please enter a valid time in format (XX:00)");
                } else {
                    valid = true;
                }
            } catch (DateTimeParseException e) {
                System.err.println("Invalid Time! Please enter '0-9' integers only.");
            }
        }
        return time;
    }
}
