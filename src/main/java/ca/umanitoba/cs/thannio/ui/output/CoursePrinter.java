package ca.umanitoba.cs.thannio.ui.output;

import ca.umanitoba.cs.thannio.domain.Course;

public class CoursePrinter {
    public CoursePrinter() {}

    public static void showCourse(Course course){
        System.out.println("\nCourse:");
        System.out.println("ID = " + course.getCourseId() + ", Name = " + course.getCourseName() + ", Difficulty = " + course.getCourseDifficulty());

    }
}
