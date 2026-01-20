# Title: Smart Study Planner 

## Author: Thanni Obafunsho (thannio@myumanitoba.ca)

## Date: Winter 2026

# Running

* The functional application can be started by running the `main` method in

## Domain Model

### Here is the diagram for my model

```mermaid
classDiagram
    
    class AssessmentType {
        ASSIGNMENT,
        QUIZ,
        MIDTERM,
        FINAL,
        LAB,
        PROJECT,
        ACTIVITIES
    }
    
    class Assessment {
        %% assessmentId must be > 0
        -int assessmentId;
        %% course cannot be null
        -Course course;
        %% name cannot be null nor blank
        -String name;
        %% type cannot be null
        -AssesmentType type;
        %% dueDate cannot be null
        -LocalDateTime dueDate;
        %% workTime must be > 0
        -int workTime;
        %% weight must be > 0
        -int weight;
        %% difficulty must be greater than 0
        -int difficulty;
    }
    
    note for Assessment "Invariants Properties: 
    * assessmentId > 0
    * course != null
    * name != null
    * name cannot be blank or empty
    * type != null
    * due != null
    * workTime > 0
    * weight > 0
    * difficulty > 0
    "

    class Course {
    %% courseName cannot be null
        -String courseName
    %% courseId must be greater than 0
        -int courseId
    %% courseDifficulty must be greater than 0 
        -int courseDifficulty
    }

    note for Course "Invariants Properties:
    * courseName != null
    * courseName.length() > 0
    * courseId > 0 
    * courseDifficulty must be >= 1
    * courseDifficulty must be <= 5
    "
    
    class Profile {
        %% maxDailyStudyMinutes must always be >= 0
        -int maxDailyStudyMinutes
        %% minutesAvailablePerDay cannot be null
        -Map <Weekdays, Integer> minutesAvailablePerDay
    }

    note for Profile "Invariants Properties:
    * maxDailyStudyMinutes >= 0
    * minutesAvailablePerDay != null
    * loop: all Integers in minutesAvailablePerDay != null
          : all WeekDays in minutesAvailablePerDay != null
    "

    class StudyBlock { 
        %% startDateTime cannot be null
        -LocalDateTime startDateTime
        %% endDateTime cannot be null
        -LocalDateTime endDateTime
        %% assessment cannot be null
        -Assessments assessment
        -boolean complete
    }
    
    note for StudyBlock "Invariants Properties:
    * startDateTime != null
    * endDateTime != null
    * startDateTime must be before endDateTime
    * assessment != null
    * duration > 0
    "
    
    class Weekday {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }
    
    class WeekPlan {
        %% weekStartDate cannot be null
        -LocalDate weekStartDate
        %% studyBlocks cannot be null
        -List<StudyBlock> studyBlocks
    }
    
    note for WeekPlan "Invariants Properties: 
    * weekStartDate != null
    * studyBlocks != null
    * loop: all Study Blocks in studyBlocks != null
    "
    
    %% these are the association relationship
    StudyBlock --> Weekday
    StudyBlock --> Assessment
    %% these are the aggregate relationships
    Profile --* Course
    %% these are the composition relationships
    Profile --* WeekPlan
    Course --* Assessment
    Assessment --* AssessmentType
    WeekPlan --*  StudyBlock

```
