package ca.umanitoba.cs.thannio.model.ui.output;

import ca.umanitoba.cs.thannio.model.StudyBlock;
import ca.umanitoba.cs.thannio.model.WeekPlan;

public class WeekPlanPrinter {
    public WeekPlanPrinter() {}

    public static void showWeekPlan(WeekPlan plan){
        System.out.println("\nWeek Plan (starting " + plan.getWeekStartDate() + "):");
        for (StudyBlock block : plan.getStudyBlocks()) {
            System.out.println(" - " + block.startDateTime().toLocalDate()
                    + " " + block.startDateTime().toLocalTime()
                    + " (" + block.durationMinutes() + " min)"
                    + " -> " + block.assessment().getName()
                    + " [" + block.assessment().getCourse().getCourseName() + "]");
        }
    }
}
