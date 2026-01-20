package ca.umanitoba.cs.thannio.domain;

import ca.umanitoba.cs.thannio.domain.exceptions.InvalidCourseDifficultyException;
import ca.umanitoba.cs.thannio.domain.exceptions.InvalidCourseIdException;
import ca.umanitoba.cs.thannio.domain.exceptions.InvalidCourseNameException;
import com.google.common.base.Preconditions;

public class Course {
    //courseName cannot be null
    private final String courseName;
    //courseId must be greater than 0
    private final int courseId;
    //courseDifficulty must be greater than 0
    private final int courseDifficulty;

    private Course(String courseName, int courseId, int courseDifficulty) {
        this.courseName = courseName;
        this.courseId = courseId;
        this.courseDifficulty = courseDifficulty;
        //postcondition for constructor: this instance is in a valid state
        checkCourse();
    }

    private void checkCourse() {
        Preconditions.checkNotNull(courseName, "Course name cannot be null");
        Preconditions.checkArgument(!courseName.isBlank(), "Course name cannot be blank");
        Preconditions.checkArgument(courseId > 0, "Course ID must be greater than 0");
        Preconditions.checkArgument(courseDifficulty >= 1, "Course difficulty must be greater than or equal to 1");
        Preconditions.checkArgument(courseDifficulty <= 5, "Course difficulty must be less than or equal to 5");
    }

    public  String getCourseName() {
        return courseName;
    }
    public int getCourseId() {
        return courseId;
    }
    public int getCourseDifficulty() {
        return courseDifficulty;
    }

    public static final class CourseBuilder {
        private String courseName;
        private int courseId;
        private int courseDifficulty;

        public CourseBuilder courseName(String courseName) throws InvalidCourseNameException {
            Preconditions.checkNotNull(courseName, "Course name cannot be null");
            if(courseName.isBlank()) {
                throw new InvalidCourseNameException();
            }
            this.courseName = courseName;
            return this;
        }

        public CourseBuilder courseId(int courseId) throws InvalidCourseIdException {
            if (courseId <= 0) {
                throw new InvalidCourseIdException();
            }
            this.courseId = courseId;
            return this;
        }

        public CourseBuilder courseDifficulty(int courseDifficulty) throws InvalidCourseDifficultyException {
            if (courseDifficulty < 1) {
                throw new InvalidCourseDifficultyException();
            }
            if(courseDifficulty > 5) {
                throw new InvalidCourseDifficultyException();
            }
            this.courseDifficulty = courseDifficulty;
            return this;
        }

        public Course build() {
            return new Course(courseName, courseId, courseDifficulty);
        }
    }
}
