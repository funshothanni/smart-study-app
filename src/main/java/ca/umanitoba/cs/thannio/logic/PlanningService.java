package ca.umanitoba.cs.thannio.logic;

import ca.umanitoba.cs.thannio.domain.Assessment;
import ca.umanitoba.cs.thannio.domain.Profile;
import ca.umanitoba.cs.thannio.domain.StudyBlock;
import ca.umanitoba.cs.thannio.domain.WeekPlan;
import com.google.common.base.Preconditions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PlanningService {
    private static final LocalTime STUDY_START_TIME = LocalTime.of(18, 0);

    public PlanningService() {}

    public WeekPlan generateWeekPlan(Profile profile, List<Assessment> assessments, LocalDate weekStartDate) {
        Preconditions.checkNotNull(profile, "Profile cannot be null");
        Preconditions.checkNotNull(assessments, "Assessments cannot be null");
        Preconditions.checkNotNull(weekStartDate, "Week Start Date cannot be null");

        WeekPlan weekPlan = new WeekPlan.WeekPlanBuilder().weekStartDate(weekStartDate).build();

        List<Assessment> undone = new ArrayList<>(assessments);
        undone.sort(Comparator.comparing(Assessment::getDueDate).thenComparing(Assessment::getWeight, Comparator.reverseOrder()));
        Map<DayOfWeek, Integer> availabilty = profile.getMinutesAvailablePerDay();
        List<Integer> minutesLeft = new ArrayList<>();
        for(Assessment a : undone) {
            minutesLeft.add(a.getEstimatedMinutes());
        }
        for(int i = 0; i < 7; i++){
            LocalDate day = weekStartDate.plusDays(i);
            DayOfWeek dayOfWeek = day.getDayOfWeek();

            int availableMinutes = availabilty.getOrDefault(dayOfWeek,0);
            availableMinutes = Math.min(availableMinutes, profile.getMaxDailyStudyMinutes());

            if(availableMinutes <= 0){
                continue;
            }

            int idx = firstAssWithMinutesLeft(minutesLeft);
            if(idx == -1){
                break;
            }
            int duration = Math.min(availableMinutes, minutesLeft.get(idx));
            LocalDateTime start = LocalDateTime.of(day, STUDY_START_TIME);
            LocalDateTime end = start.plusMinutes(duration);

            StudyBlock block = new StudyBlock(start, end, undone.get(idx), false);
            weekPlan.addStudyBlock(block);
            minutesLeft.set(idx, minutesLeft.get(idx) - duration);
        }
        return weekPlan;
    }

    private int firstAssWithMinutesLeft(List<Integer> minutesLeft) {
        for (int i = 0; i < minutesLeft.size(); i++) {
            if (minutesLeft.get(i) > 0) return i;
        }
        return -1;
    }
}
