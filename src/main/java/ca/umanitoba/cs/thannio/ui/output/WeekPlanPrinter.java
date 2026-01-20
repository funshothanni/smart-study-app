package ca.umanitoba.cs.thannio.ui.output;

import ca.umanitoba.cs.thannio.domain.StudyBlock;
import ca.umanitoba.cs.thannio.domain.WeekPlan;

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
