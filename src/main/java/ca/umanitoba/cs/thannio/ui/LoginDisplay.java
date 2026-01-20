package ca.umanitoba.cs.thannio.ui;

import ca.umanitoba.cs.thannio.domain.*;
import ca.umanitoba.cs.thannio.domain.exceptions.*;
import ca.umanitoba.cs.thannio.logic.LoginService;
import ca.umanitoba.cs.thannio.logic.PlanningService;
import ca.umanitoba.cs.thannio.logic.SessionManager;
import ca.umanitoba.cs.thannio.ui.output.AssessmentPrinter;
import ca.umanitoba.cs.thannio.ui.output.CoursePrinter;
import ca.umanitoba.cs.thannio.ui.output.ProfilePrinter;
import ca.umanitoba.cs.thannio.ui.output.WeekPlanPrinter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static ca.umanitoba.cs.thannio.ui.InputPrompts.promptNonBlankUpperString;
import static ca.umanitoba.cs.thannio.ui.InputPrompts.promptPositiveInt;

public class LoginDisplay {
    private LoginDisplay() {}
    public static void handleStart(){
        try {
            AppDatabase database = new AppDatabase();
            Profile profile = new Profile.ProfileBuilder().name("Thanni Funsho").profileId(1).pin("3465").maxDailyStudyMinutes(180).build(); // 3 hours max per day
            database.addProfile(profile);
            profile.setAvailableMinutes(DayOfWeek.MONDAY, 120);
            profile.setAvailableMinutes(DayOfWeek.TUESDAY, 90);
            profile.setAvailableMinutes(DayOfWeek.WEDNESDAY, 180);

            Course comp2140 = new Course.CourseBuilder().courseId(2140).courseName("COMP 2140").courseDifficulty(4).build();
            database.addCourse(comp2140);

            Assessment midterm = new Assessment.AssessmentBuilder().assessmentId(1).course(comp2140).name("Midterm 1").type(AssessmentType.MIDTERM).dueDate(LocalDateTime.now().plusDays(10)).estimatedMinutes(600).weight(25).difficulty(4).build(); // 10 hours
            database.addAssessment(midterm);

            System.out.println("=== SMART STUDY APP ===");
            ProfilePrinter.showProfile(profile);
            CoursePrinter.showCourse(comp2140);
            AssessmentPrinter.showAssessment(midterm);
            PlanningService plannerService = new PlanningService();

            LocalDate weekStart = LocalDate.now();
            WeekPlan plan = plannerService.generateWeekPlan(profile, List.of(midterm), weekStart);
            WeekPlanPrinter.showWeekPlan(plan);

        } catch (InvalidMaxDailyStudyMinutesException e){
            System.err.println("Invalid Maximum Daily Study Minutes!");
        } catch (InvalidCourseNameException e){
            System.err.println("Invalid Course Name!");
        } catch (InvalidCourseIdException e){
            System.err.println("Invalid Course ID!");
        } catch (InvalidCourseDifficultyException e){
            System.err.println("Invalid Course Difficulty!");
        } catch (InvalidAssessmentIdException e){
            System.err.println("Invalid Assessment ID!");
        } catch (InvalidAssessmentNameException e){
            System.err.println("Invalid Assessment Name!");
        } catch (InvalidAssessmentEstimatedMinutesException e){
            System.err.println("Invalid Assessment Estimated Minutes!");
        } catch (InvalidAssessmentWeightException e){
            System.err.println("Invalid Assessment Weight!");
        } catch (InvalidAssessmentDifficultyException e){
            System.err.println("Invalid Assessment Difficulty!");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
    }

    public static void handleLogin(Scanner sc, Profile profile, AppDatabase database) {
        int MAX_PIN_ATTEMPTS = 5; //maximum number of tries to log in into an existing account

        if (SessionManager.activeSession()) {
            System.out.println("There's an active account logged in! Please LOGOUT first.");
        } else {
            System.out.print("WELCOME TO ");
            ProfilePrinter.showProfile(profile);
                if (database.getAllProfiles().isEmpty()) {
                    System.out.println("There are no existing profiles in the database! Enter 'LOGIN' then 'CREATE' to create a new account'");
                } else {
                    Profile a = null;
                    boolean loggedIn = false;

                    while (!loggedIn && MAX_PIN_ATTEMPTS > 0) {
                        int profileId = promptPositiveInt(sc, "Enter Account ID (with no starting 0s)");
                        String pin = promptNonBlankUpperString(sc, "Pin");

                        try {
                            a = LoginService.login(database, profileId, pin);
                            loggedIn = true;
                            System.out.println("Login Successful! Welcome back, " + a.getName() + ".");
                        } catch (ProfileNotFoundException e) {
                            System.err.println("Account not found in database! Please try again.");
                        } catch (InvalidProfilePinException e) {
                            MAX_PIN_ATTEMPTS--; //decrements trials each failed attempt
                            System.err.println("Invalid pin! Pin is incorrect or non-4 digit number. Please try again. " + MAX_PIN_ATTEMPTS + " tries left.");
                        }
                    }
                    // only login if the user entered the right pin
                    if (loggedIn && a != null) {
                        SessionManager.startSession(a);
                    } else if (!loggedIn) {
                        System.err.println("Too many failed login attempts. Returning to main menu.");
                    }
                }
        }
    }

}
