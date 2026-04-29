package ca.umanitoba.cs.thannio.web;

import ca.umanitoba.cs.thannio.domain.*;
import ca.umanitoba.cs.thannio.domain.exceptions.*;
import ca.umanitoba.cs.thannio.logic.PlanningService;
import io.javalin.Javalin;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class SmartStudyWebAppMain {
    private static AppDatabase database;
    private static Profile currentProfile;

    public static void main(String[] args) {
        setupSampleData();

        Javalin app = Javalin.create().start(7070);

        app.get("/", ctx -> ctx.html(renderDashboard()));

        app.post("/courses", ctx -> {
            try {
                int courseId = Integer.parseInt(ctx.formParam("courseId"));
                String courseName = ctx.formParam("courseName");
                int courseDifficulty = Integer.parseInt(ctx.formParam("courseDifficulty"));

                Course course = new Course.CourseBuilder()
                        .courseId(courseId)
                        .courseName(courseName)
                        .courseDifficulty(courseDifficulty)
                        .build();

                database.addCourse(course);

                ctx.redirect("/");
            } catch (Exception e) {
                ctx.html("<h1>Error adding course</h1><p>" + e.getMessage() + "</p><a href='/'>Back</a>");
            }
        });

        app.post("/assessments", ctx -> {
            try {
                int assessmentId = Integer.parseInt(ctx.formParam("assessmentId"));
                int courseId = Integer.parseInt(ctx.formParam("courseId"));
                String assessmentName = ctx.formParam("assessmentName");
                AssessmentType assessmentType = AssessmentType.valueOf(ctx.formParam("assessmentType"));
                LocalDateTime dueDate = LocalDateTime.parse(ctx.formParam("dueDate"));
                int estimatedMinutes = Integer.parseInt(ctx.formParam("estimatedMinutes"));
                int weight = Integer.parseInt(ctx.formParam("weight"));
                int difficulty = Integer.parseInt(ctx.formParam("difficulty"));

                Course course = database.getCourse(courseId);

                Assessment assessment = new Assessment.AssessmentBuilder()
                        .assessmentId(assessmentId)
                        .course(course)
                        .name(assessmentName)
                        .type(assessmentType)
                        .dueDate(dueDate)
                        .estimatedMinutes(estimatedMinutes)
                        .weight(weight)
                        .difficulty(difficulty)
                        .build();

                database.addAssessment(assessment);

                ctx.redirect("/");
            } catch (Exception e) {
                ctx.html("<h1>Error adding assessment</h1><p>" + e.getMessage() + "</p><a href='/'>Back</a>");
            }
        });

        app.post("/availability", ctx -> {
            try {
                int monday = Integer.parseInt(ctx.formParam("monday"));
                int Tuesday = Integer.parseInt(ctx.formParam("tuesday"));
                int wednesday = Integer.parseInt(ctx.formParam("wednesday"));
                int thursday = Integer.parseInt(ctx.formParam("thursday"));
                int friday = Integer.parseInt(ctx.formParam("friday"));
                int saturday = Integer.parseInt(ctx.formParam("saturday"));
                int sunday = Integer.parseInt(ctx.formParam("sunday"));

                currentProfile.setAvailableMinutes(DayOfWeek.MONDAY, monday);
                currentProfile.setAvailableMinutes(DayOfWeek.TUESDAY, Tuesday);
                currentProfile.setAvailableMinutes(DayOfWeek.WEDNESDAY, wednesday);
                currentProfile.setAvailableMinutes(DayOfWeek.THURSDAY, thursday);
                currentProfile.setAvailableMinutes(DayOfWeek.FRIDAY, friday);
                currentProfile.setAvailableMinutes(DayOfWeek.SATURDAY, saturday);
                currentProfile.setAvailableMinutes(DayOfWeek.SUNDAY, sunday);

                ctx.redirect("/");
            } catch (Exception e) {
                ctx.html("<h1>Error updating availability</h1><p>" + e.getMessage() + "</p><a href='/'>Back</a>");
            }
        });

        app.post("/profile", ctx -> {
            try {
                int profileId = Integer.parseInt(ctx.formParam("profileId"));
                String name = ctx.formParam("name");
                String pin = ctx.formParam("pin");
                int maxDailyStudyMinutes = Integer.parseInt(ctx.formParam("maxDailyStudyMinutes"));

                Profile newProfile = new Profile.ProfileBuilder()
                        .profileId(profileId)
                        .name(name)
                        .pin(pin)
                        .maxDailyStudyMinutes(maxDailyStudyMinutes)
                        .build();

                currentProfile = newProfile;

                try {
                    database.addProfile(newProfile);
                } catch (DuplicateProfileException ignored) {
                    // If the profile ID already exists, we still update currentProfile.
                }

                ctx.redirect("/");
            } catch (Exception e) {
                ctx.html("<h1>Error updating profile</h1><p>" + e.getMessage() + "</p><a href='/'>Back</a>");
            }
        });
    }

    private static void setupSampleData() {
        try {
            database = new AppDatabase();

            currentProfile = new Profile.ProfileBuilder()
                    .name("Thanni Funsho")
                    .profileId(1)
                    .pin("3465")
                    .maxDailyStudyMinutes(180)
                    .build();

            database.addProfile(currentProfile);

            currentProfile.setAvailableMinutes(DayOfWeek.MONDAY, 120);
            currentProfile.setAvailableMinutes(DayOfWeek.TUESDAY, 90);
            currentProfile.setAvailableMinutes(DayOfWeek.WEDNESDAY, 180);
            currentProfile.setAvailableMinutes(DayOfWeek.THURSDAY, 60);
            currentProfile.setAvailableMinutes(DayOfWeek.FRIDAY, 90);

            Course comp2140 = new Course.CourseBuilder()
                    .courseId(2140)
                    .courseName("COMP 2140")
                    .courseDifficulty(4)
                    .build();

            database.addCourse(comp2140);

            Assessment midterm = new Assessment.AssessmentBuilder()
                    .assessmentId(1)
                    .course(comp2140)
                    .name("Midterm 1")
                    .type(AssessmentType.MIDTERM)
                    .dueDate(LocalDateTime.now().plusDays(10))
                    .estimatedMinutes(600)
                    .weight(25)
                    .difficulty(4)
                    .build();

            database.addAssessment(midterm);

        } catch (Exception e) {
            throw new RuntimeException("Could not set up sample data", e);
        }
    }

    private static String renderDashboard() {
        PlanningService planningService = new PlanningService();

        WeekPlan plan = planningService.generateWeekPlan(
                currentProfile,
                new ArrayList<>(database.getAllAssessments().values()),
                LocalDate.now()
        );

        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Smart Study App</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: #f4f6fb;
                            margin: 0;
                            padding: 0;
                            color: #222;
                        }
                        .container {
                            width: 90%;
                            max-width: 1000px;
                            margin: 40px auto;
                        }
                        .header {
                            background: #1f2937;
                            color: white;
                            padding: 30px;
                            border-radius: 16px;
                            margin-bottom: 24px;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 34px;
                        }
                        .header p {
                            margin: 8px 0 0;
                            opacity: 0.9;
                        }
                        .grid {
                            display: grid;
                            grid-template-columns: repeat(3, 1fr);
                            gap: 16px;
                            margin-bottom: 24px;
                        }
                        .card {
                            background: white;
                            padding: 20px;
                            border-radius: 16px;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                        }
                        .card h2 {
                            margin-top: 0;
                            font-size: 20px;
                        }
                        .stat {
                            font-size: 32px;
                            font-weight: bold;
                            margin-top: 8px;
                        }
                        table {
                            width: 100%;
                            border-collapse: collapse;
                            margin-top: 12px;
                        }
                        th, td {
                            padding: 12px;
                            border-bottom: 1px solid #ddd;
                            text-align: left;
                        }
                        th {
                            background: #f1f5f9;
                        }
                        input, select, button {
                            padding: 10px;
                            border-radius: 8px;
                            border: 1px solid #cbd5e1;
                            font-size: 14px;
                        }
                
                        button {
                            background: #1f2937;
                            color: white;
                            cursor: pointer;
                            border: none;
                        }
                
                        button:hover {
                            background: #374151;
                        }
                        .tag {
                            display: inline-block;
                            background: #e5e7eb;
                            padding: 4px 8px;
                            border-radius: 999px;
                            font-size: 13px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Smart Study App</h1>
                            <p>Plan your courses, deadlines, and weekly study time.</p>
                        </div>
                """);

        html.append("<div class='grid'>");

        html.append("<div class='card'>");
        html.append("<h2>Profile</h2>");
        html.append("<p><strong>Name:</strong> ").append(currentProfile.getName()).append("</p>");
        html.append("<p><strong>Profile ID:</strong> ").append(currentProfile.getProfileId()).append("</p>");
        html.append("<p><strong>Max Daily Study:</strong> ").append(currentProfile.getMaxDailyStudyMinutes()).append(" minutes</p>");

        html.append("""
        <form action="/profile" method="post" style="margin-top: 15px;">
            <label>Name</label><br>
            <input type="text" name="name" required><br><br>

            <label>Profile ID</label><br>
            <input type="number" name="profileId" min="1" required><br><br>

            <label>PIN</label><br>
            <input type="text" name="pin" minlength="4" maxlength="4" pattern="\\d{4}" required><br><br>

            <label>Max Daily Study Minutes</label><br>
            <input type="number" name="maxDailyStudyMinutes" min="0" required><br><br>

            <button type="submit">Update Profile</button>
        </form>
        """);

        html.append("</div>");

        html.append("<div class='card'>");
        html.append("<h2>Courses</h2>");
        html.append("<div class='stat'>").append(database.getAllCourses().size()).append("</div>");
        html.append("</div>");

        html.append("<div class='card'>");
        html.append("<h2>Assessments</h2>");
        html.append("<div class='stat'>").append(database.getAllAssessments().size()).append("</div>");
        html.append("</div>");

        html.append("</div>");

        html.append("<div class='card'>");
        html.append("<h2>Weekly Availability</h2>");
        html.append("<p>Enter how many minutes you can study each day.</p>");

        html.append("""
        <form action="/availability" method="post" style="margin-bottom: 20px;">
            <label>Monday</label><br>
            <input type="number" name="monday" min="0" required><br><br>

            <label>Tuesday</label><br>
            <input type="number" name="tuesday" min="0" required><br><br>

            <label>Wednesday</label><br>
            <input type="number" name="wednesday" min="0" required><br><br>

            <label>Thursday</label><br>
            <input type="number" name="thursday" min="0" required><br><br>

            <label>Friday</label><br>
            <input type="number" name="friday" min="0" required><br><br>

            <label>Saturday</label><br>
            <input type="number" name="saturday" min="0" required><br><br>

            <label>Sunday</label><br>
            <input type="number" name="sunday" min="0" required><br><br>

            <button type="submit">Update Availability</button>
        </form>
        """);

        html.append("<p><strong>Current Availability:</strong> ")
                .append(currentProfile.getMinutesAvailablePerDay())
                .append("</p>");

        html.append("</div>");
        html.append("<br>");

        html.append("<div class='card'>");
        html.append("<h2>Courses</h2>");
        html.append("""
        <form action="/courses" method="post" style="margin-bottom: 20px;">
            <label>Course ID</label><br>
            <input type="number" name="courseId" required><br><br>

            <label>Course Name</label><br>
            <input type="text" name="courseName" required><br><br>

            <label>Difficulty (1-5)</label><br>
            <input type="number" name="courseDifficulty" min="1" max="5" required><br><br>

            <button type="submit">Add Course</button>
        </form>
        """);
        html.append("<table>");
        html.append("<tr><th>ID</th><th>Name</th><th>Difficulty</th></tr>");

        for (Course course : database.getAllCourses().values()) {
            html.append("<tr>");
            html.append("<td>").append(course.getCourseId()).append("</td>");
            html.append("<td>").append(course.getCourseName()).append("</td>");
            html.append("<td>").append(course.getCourseDifficulty()).append("/5</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</div>");

        html.append("<br>");

        html.append("<div class='card'>");
        html.append("<h2>Assessments</h2>");
        html.append("""
        <form action="/assessments" method="post" style="margin-bottom: 20px;">
            <label>Assessment ID</label><br>
            <input type="number" name="assessmentId" required><br><br>

            <label>Course</label><br>
            <select name="courseId" required>
        """);

        for (Course course : database.getAllCourses().values()) {
            html.append("<option value='")
                    .append(course.getCourseId())
                    .append("'>")
                    .append(course.getCourseName())
                    .append("</option>");
        }

        html.append("""
            </select><br><br>

            <label>Assessment Name</label><br>
            <input type="text" name="assessmentName" required><br><br>

            <label>Assessment Type</label><br>
            <select name="assessmentType" required>
                <option value="ASSIGNMENT">Assignment</option>
                <option value="QUIZ">Quiz</option>
                <option value="MIDTERM">Midterm</option>
                <option value="FINAL">Final</option>
                <option value="LAB">Lab</option>
                <option value="PROJECT">Project</option>
                <option value="ACTIVITIES">Activities</option>
                <option value="OTHER">Other</option>
            </select><br><br>

            <label>Due Date</label><br>
            <input type="datetime-local" name="dueDate" required><br><br>

            <label>Estimated Minutes</label><br>
            <input type="number" name="estimatedMinutes" min="1" required><br><br>

            <label>Weight (%)</label><br>
            <input type="number" name="weight" min="1" max="100" required><br><br>

            <label>Difficulty (1-5)</label><br>
            <input type="number" name="difficulty" min="1" max="5" required><br><br>

            <button type="submit">Add Assessment</button>
        </form>
        """);
        html.append("<table>");
        html.append("<tr><th>ID</th><th>Name</th><th>Course</th><th>Type</th><th>Due Date</th><th>Estimated Time</th><th>Weight</th><th>Difficulty</th></tr>");

        for (Assessment assessment : database.getAllAssessments().values()) {
            html.append("<tr>");
            html.append("<td>").append(assessment.getAssessmentId()).append("</td>");
            html.append("<td>").append(assessment.getName()).append("</td>");
            html.append("<td>").append(assessment.getCourse().getCourseName()).append("</td>");
            html.append("<td><span class='tag'>").append(assessment.getType()).append("</span></td>");
            html.append("<td>").append(assessment.getDueDate()).append("</td>");
            html.append("<td>").append(assessment.getEstimatedMinutes()).append(" min</td>");
            html.append("<td>").append(assessment.getWeight()).append("%</td>");
            html.append("<td>").append(assessment.getDifficulty()).append("/5</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</div>");

        html.append("<br>");

        html.append("<div class='card'>");
        html.append("<h2>Generated Weekly Study Plan</h2>");

        if (plan.getStudyBlocks().isEmpty()) {
            html.append("<p>No study blocks generated yet. Check your availability and assessments.</p>");
        } else {
            html.append("<table>");
            html.append("<tr><th>Date</th><th>Start</th><th>End</th><th>Duration</th><th>Assessment</th><th>Course</th></tr>");

            for (StudyBlock block : plan.getStudyBlocks()) {
                html.append("<tr>");
                html.append("<td>").append(block.startDateTime().toLocalDate()).append("</td>");
                html.append("<td>").append(block.startDateTime().toLocalTime()).append("</td>");
                html.append("<td>").append(block.endDateTime().toLocalTime()).append("</td>");
                html.append("<td>").append(block.durationMinutes()).append(" min</td>");
                html.append("<td>").append(block.assessment().getName()).append("</td>");
                html.append("<td>").append(block.assessment().getCourse().getCourseName()).append("</td>");
                html.append("</tr>");
            }

            html.append("</table>");
        }

        html.append("</div>");

        html.append("""
                    </div>
                </body>
                </html>
                """);

        return html.toString();
    }
}