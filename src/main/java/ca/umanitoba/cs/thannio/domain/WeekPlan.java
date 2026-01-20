package ca.umanitoba.cs.thannio.domain;

import com.google.common.base.Preconditions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeekPlan {
    //weekStartDate cannot be null
    private final LocalDate weekStartDate;
    //studyBlock cannot be null
    private final List<StudyBlock> studyBlocks;

    private WeekPlan(LocalDate weekStartDate) {
        this.weekStartDate = weekStartDate;
        this.studyBlocks = new ArrayList<>();
        //postcondition for constructor: this instance is in a valid state
        checkWeekPlan();
    }

    private void checkWeekPlan(){
        Preconditions.checkNotNull(weekStartDate, "Start Date of the Week cannot be null");
        Preconditions.checkNotNull(studyBlocks, "Study Blocks cannot be null");
        for (StudyBlock studyBlock : studyBlocks) {
            Preconditions.checkNotNull(studyBlock, "Study Block cannot be null");
        }
    }

    /**
     * this method adds a new study block to a week's plan of studying
     * @param studyBlock
     */
    public void addStudyBlock(StudyBlock studyBlock) {
        Preconditions.checkNotNull(studyBlock, "Study Block cannot be null");
        studyBlocks.add(studyBlock);
        checkWeekPlan();
    }

    /**
     * this method calculates the total minutes in a particular day
     * @param date
     * @return
     */
    public long getMinutesScheduledOn(LocalDate date) {
        return studyBlocks.stream()
                .filter(b -> b.startDateTime().toLocalDate().equals(date))
                .mapToLong(StudyBlock::durationMinutes)
                .sum();
    }

    public LocalDate getWeekStartDate() {
        return weekStartDate;
    }

    public List<StudyBlock> getStudyBlocks() {
        return Collections.unmodifiableList(studyBlocks);
    }

    public static final class WeekPlanBuilder {
        private LocalDate weekStartDate;

        public WeekPlanBuilder weekStartDate(LocalDate weekStartDate) {
            Preconditions.checkNotNull(weekStartDate, "Week Start Date cannot be null");
            this.weekStartDate = weekStartDate;
            return this;
        }

        public WeekPlan build() {
            return new WeekPlan(weekStartDate);
        }
    }
}
