package ca.umanitoba.cs.thannio.domain;

import ca.umanitoba.cs.thannio.domain.exceptions.*;
import com.google.common.base.Preconditions;

import java.time.LocalDateTime;

public class Assessment {
    private final int assessmentId;
    private final Course course;
    private final String name;
    private final AssessmentType type;
    private final LocalDateTime dueDate;
    private final int estimatedMinutes;
    private final int weight;
    private final int difficulty;

    private Assessment(int assessmentId, Course course, String name, AssessmentType type, LocalDateTime dueDate, int estimatedMinutes, int weight, int difficulty) {
        this.assessmentId = assessmentId;
        this.course = course;
        this.name = name;
        this.type = type;
        this.dueDate = dueDate;
        this.estimatedMinutes = estimatedMinutes;
        this.weight = weight;
        this.difficulty = difficulty;
        //postcondition for constructor: this instance is in a valid state
        checkAssessment();
    }

    private void checkAssessment() {
        Preconditions.checkArgument(assessmentId > 0, "Assessment id must be greater than 0.");
        Preconditions.checkNotNull(course, "Course must not be null.");
        Preconditions.checkNotNull(name, "Assessment Name cannot be null.");
        Preconditions.checkArgument(!name.isBlank(), "Assessment Name cannot be blank.");
        Preconditions.checkNotNull(type, "Assessment Type cannot be null.");
        Preconditions.checkNotNull(dueDate, "Assessment Date cannot be null.");
        Preconditions.checkArgument(estimatedMinutes > 0, "Assessment Estimated Time must be greater than 0.");
        Preconditions.checkArgument(weight > 0, "Assessment Weight must be greater than 0.");
        Preconditions.checkArgument(difficulty > 0, "Assessment Difficulty must be greater than 0.");
    }

    public int getAssessmentId() {
        return assessmentId;
    }

    public Course getCourse() {
        return course;
    }

    public String getName() {
        return name;
    }

    public AssessmentType getType() {
        return type;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public int getWeight() {
        return weight;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public static final class AssessmentBuilder {
        //assessmentId must be > 0
        private int assessmentId;
        //course cannot be null
        private Course course;
        //name cannot be null nor blank
        private String name;
        //type cannot be null
        private AssessmentType type;
        //dueDate cannot be null
        private LocalDateTime dueDate;
        //estimatedMinutes must be > 0
        private int estimatedMinutes;
        //weight must be > 0
        private int weight;
        //difficulty must be greater than 0
        private int difficulty;

        public AssessmentBuilder assessmentId(int assessmentId) throws InvalidAssessmentIdException {
            if(assessmentId <= 0) {
                throw new InvalidAssessmentIdException();
            }
            this.assessmentId = assessmentId;
            return this;
        }

        public AssessmentBuilder course(Course course) {
            Preconditions.checkNotNull(course, "Course must not be null.");
            this.course = course;
            return this;
        }

        public AssessmentBuilder name(String name) throws InvalidAssessmentNameException {
            Preconditions.checkNotNull(name, "Name must not be null.");
            if(name.isBlank()) {
                throw new InvalidAssessmentNameException();
            }
            this.name = name;
            return this;
        }

        public AssessmentBuilder type(AssessmentType type) {
            Preconditions.checkNotNull(type, "Type must not be null.");
            this.type = type;
            return this;
        }

        public AssessmentBuilder dueDate(LocalDateTime dueDate) {
            Preconditions.checkNotNull(dueDate, "Due date must not be null.");
            this.dueDate = dueDate;
            return this;
        }

        public AssessmentBuilder estimatedMinutes(int estimatedMinutes) throws InvalidAssessmentEstimatedMinutesException {
            if(estimatedMinutes <= 0) {
                throw new InvalidAssessmentEstimatedMinutesException();
            }
            this.estimatedMinutes = estimatedMinutes;
            return this;
        }

        public AssessmentBuilder weight(int weight) throws InvalidAssessmentWeightException {
            if(weight < 1) {
                throw new InvalidAssessmentWeightException();
            }
            if(weight > 100) {
                throw new InvalidAssessmentWeightException();
            }
            this.weight = weight;
            return this;
        }

        public AssessmentBuilder difficulty(int difficulty) throws InvalidAssessmentDifficultyException {
            if(difficulty < 1) {
                throw new InvalidAssessmentDifficultyException();
            }
            if(difficulty > 5){
                throw new InvalidAssessmentDifficultyException();
            }
            this.difficulty = difficulty;
            return this;
        }

        public Assessment build() {
            return new Assessment(assessmentId, course, name, type, dueDate, estimatedMinutes, weight, difficulty);
        }
    }
}
