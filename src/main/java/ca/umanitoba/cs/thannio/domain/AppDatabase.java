package ca.umanitoba.cs.thannio.domain;

import ca.umanitoba.cs.thannio.domain.exceptions.*;
import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AppDatabase {
    private final Map<Integer, Assessment> allAssessments;
    private final Map<Integer, Course> allCourses;
    private final Map<Integer, Profile> allProfiles;

    public AppDatabase(){
        this.allAssessments = new LinkedHashMap<>();
        this.allCourses = new LinkedHashMap<>();
        this.allProfiles = new LinkedHashMap<>();
        checkAppDatabase();
    }

    private void checkAppDatabase() {
        Preconditions.checkNotNull(allAssessments, "Assessments cannot be null");
        for(Map.Entry<Integer, Assessment> entry : allAssessments.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), "Assessment ID cannot be null");
            Preconditions.checkArgument(entry.getKey() > 0, "Assessment ID cannot be negative");
            Preconditions.checkNotNull(entry.getValue(), "Assessment cannot be null");
        }
        Preconditions.checkNotNull(allCourses, "Courses cannot be null");
        for(Map.Entry<Integer, Course> entry : allCourses.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), "Course ID cannot be null");
            Preconditions.checkArgument(entry.getKey() > 0, "Course ID cannot be negative");
            Preconditions.checkNotNull(entry.getValue(), "Course cannot be null");
        }
        Preconditions.checkNotNull(allProfiles, "Profiles cannot be null");
        for(Map.Entry<Integer, Profile> entry : allProfiles.entrySet()) {
            Preconditions.checkNotNull(entry.getKey(), "Profile ID cannot be null");
            Preconditions.checkArgument(entry.getKey() > 0, "Profile ID cannot be negative");
            Preconditions.checkNotNull(entry.getValue(), "Profile cannot be null");
        }
    }

    public Map <Integer, Assessment> getAllAssessments() { return Collections.unmodifiableMap(this.allAssessments); }

    public Map <Integer, Course> getAllCourses() { return Collections.unmodifiableMap(this.allCourses); }

    public Map <Integer, Profile> getAllProfiles() { return Collections.unmodifiableMap(this.allProfiles); }

    public Assessment getAssessment(int assessmentId) throws AssessmentNotFoundException {
        Assessment assessment = this.allAssessments.get(assessmentId);
        if (assessment == null) {
            throw new AssessmentNotFoundException();
        }
        return assessment;
    }

    public Course getCourse(int courseId) throws CourseNotFoundException {
        Course course = this.allCourses.get(courseId);
        if(course == null) {
            throw new CourseNotFoundException();
        }
        return course;
    }

    public Profile getProfile(int profileId) throws ProfileNotFoundException {
        Profile profile = this.allProfiles.get(profileId);
        if(profile == null) {
            throw new ProfileNotFoundException();
        }
        return profile;
    }

    public void addAssessment(Assessment assessment) throws DuplicateAssessmentException {
        Preconditions.checkNotNull(assessment, "Assessment cannot be null");
        if(allAssessments.containsKey(assessment.getAssessmentId())){
            throw new DuplicateAssessmentException();
        }
        allAssessments.put(assessment.getAssessmentId(), assessment);
        checkAppDatabase();
    }

    public void addCourse(Course course) throws DuplicateCourseException {
        Preconditions.checkNotNull(course, "Course cannot be null");
        if(allCourses.containsKey(course.getCourseId())){
            throw new DuplicateCourseException();
        }
        allCourses.put(course.getCourseId(), course);
        checkAppDatabase();
    }

    public void addProfile(Profile profile) throws DuplicateProfileException {
        Preconditions.checkNotNull(profile, "Profile cannot be null");
        if(allProfiles.containsKey(profile.getProfileId())){
            throw new DuplicateProfileException();
        }
        allProfiles.put(profile.getProfileId(), profile);
        checkAppDatabase();
    }
}
