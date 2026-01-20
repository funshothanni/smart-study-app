package ca.umanitoba.cs.thannio.domain;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;

public record StudyBlock(LocalDateTime startDateTime, LocalDateTime endDateTime, Assessment assessment, boolean complete) {

    public StudyBlock {
        //postcondition for constructor: this instance is in a valid state
        Preconditions.checkNotNull(startDateTime, "Start Date Time cannot be null");
        Preconditions.checkNotNull(endDateTime, "End Date Time cannot be null");
        Preconditions.checkArgument(startDateTime.isBefore(endDateTime), "Start Date Time must be before End Date Time");
        Preconditions.checkNotNull(assessment, "Assessment cannot be null.");
    }

    /**
     * this a helper method to calculate the minutes between a start and end time
     * @return
     */
    public long durationMinutes() {
        return java.time.Duration.between(startDateTime, endDateTime).toMinutes();
    }

    public static class StudyBlockBuilder {
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private Assessment assessment;
        private boolean complete;

        public StudyBlockBuilder() {}

        public StudyBlockBuilder startDateTime(LocalDateTime startDateTime) {
            Preconditions.checkNotNull(startDateTime, "Start Date Time cannot be null");
            this.startDateTime = startDateTime;
            return this;
        }

        public StudyBlockBuilder endDateTime(LocalDateTime endDateTime) {
            Preconditions.checkNotNull(endDateTime, "End Date Time cannot be null");
            this.endDateTime = endDateTime;
            return this;
        }

        public StudyBlockBuilder assessment(Assessment assessment) {
            Preconditions.checkNotNull(assessment, "Assessment cannot be null.");
            this.assessment = assessment;
            return this;
        }

        public StudyBlockBuilder complete(boolean complete) {
            this.complete = complete;
            return this;
        }

        public StudyBlock build() {
            return new StudyBlock(startDateTime, endDateTime, assessment, complete);
        }
    }
}

