package ca.umanitoba.cs.thannio.model.ui.output;

import ca.umanitoba.cs.thannio.model.Assessment;

public class AssessmentPrinter {

    public AssessmentPrinter() {
    }

    public static void showAssessment(Assessment assessment) {
        System.out.println("\nAssessment:");
        System.out.println("ID = " + assessment.getAssessmentId() + ", Course = " + assessment.getCourse().getCourseName() + ", Name = " + assessment.getName() + ", Type = " + assessment.getType() + ", Due Date = " + assessment.getDueDate() + ", Estimated Minutes = " + assessment.getEstimatedMinutes() + ", Weight = " + assessment.getWeight() + ", Difficulty = " + assessment.getDifficulty());

    }
}
