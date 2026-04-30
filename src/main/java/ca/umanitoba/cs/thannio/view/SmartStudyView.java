package ca.umanitoba.cs.thannio.view;

import ca.umanitoba.cs.thannio.domain.Assessment;
import ca.umanitoba.cs.thannio.domain.Course;
import ca.umanitoba.cs.thannio.domain.Profile;
import ca.umanitoba.cs.thannio.domain.SmartStudyModel;
import ca.umanitoba.cs.thannio.domain.StudyBlock;
import ca.umanitoba.cs.thannio.domain.WeekPlan;

public class SmartStudyView {

    private SmartStudyView() {
    }


    public static String renderLoginPage() {
        return pageStart("Login")
                + header("Smart Study App", false)
                + """
            <div class="card">
                <h2>Login</h2>
                <form action="/login" method="post">
                    <label>Profile ID</label><br>
                    <input type="number" name="profileId" min="1" required><br><br>

                    <label>PIN</label><br>
                    <input type="text" name="pin" minlength="4" maxlength="4" pattern="\\d{4}" required><br><br>

                    <button type="submit">Login</button>
                </form>

                <p>New here? <a href="/register">Create an account</a></p>
            </div>
            """
                + pageEnd();
    }

    public static String renderRegisterPage() {
        return pageStart("Create Account")
                + header("Create Account", false)
                + """
            <div class="card">
                <h2>Create Profile</h2>
                <form action="/register" method="post">
                    <label>Name</label><br>
                    <input type="text" name="name" required><br><br>

                    <label>PIN</label><br>
                    <input type="text" name="pin" minlength="4" maxlength="4" pattern="\\d{4}" required><br><br>

                    <label>Max Daily Study Minutes</label><br>
                    <input type="number" name="maxDailyStudyMinutes" min="0" required><br><br>

                    <button type="submit">Create Account</button>
                </form>

                <p>Already have an account? <a href="/login">Login</a></p>
            </div>
            """
                + pageEnd();
    }

    public static String renderAccountCreatedPage(int profileId) {
        return pageStart("Account Created")
                + header("Account Created", false)
                + """
            <div class="card">
                <h2>Your account was created!</h2>
                <p>Your Profile ID is:</p>
                <h1>""" + profileId + """
                </h1>
                <p>Save this ID. You will use it to log in.</p>
                <a href="/dashboard">Go to Dashboard</a>
            </div>
            """
                + pageEnd();
    }

    public static String renderDashboardPage(SmartStudyModel model) {
        Profile profile = model.getCurrentProfile();
        WeekPlan plan = model.generateCurrentWeekPlan();

        StringBuilder html = new StringBuilder();

        html.append(pageStart("Dashboard"));
        html.append(header("Dashboard", true));

        html.append("<div class='card'>");
        html.append("<h2>Welcome, ").append(profile.getName()).append("</h2>");
        html.append("<p><strong>Profile ID:</strong> ").append(profile.getProfileId()).append("</p>");
        html.append("<p><strong>Max Daily Study:</strong> ").append(profile.getMaxDailyStudyMinutes()).append(" minutes</p>");
        html.append("<p><strong>Courses:</strong> ").append(model.getDatabase().getAllCourses().size()).append("</p>");
        html.append("<p><strong>Assessments:</strong> ").append(model.getDatabase().getAllAssessments().size()).append("</p>");
        html.append("</div>");

        html.append("<div class='card'>");
        html.append("<h2>Generated Weekly Study Plan</h2>");

        if (plan.getStudyBlocks().isEmpty()) {
            html.append("<p>No study blocks generated yet. Add assessments and availability first.</p>");
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
        html.append(pageEnd());

        return html.toString();
    }

    public static String renderCoursesPage(SmartStudyModel model) {
        StringBuilder html = new StringBuilder();

        html.append(pageStart("Courses"));
        html.append(header("Courses", true));

        html.append("<div class='card'>");
        html.append("<h2>Add Course</h2>");
        html.append("""
            <form action="/courses" method="post">
                <label>Course Name</label><br>
                <input type="text" name="courseName" required><br><br>

                <label>Difficulty (1-5)</label><br>
                <input type="number" name="courseDifficulty" min="1" max="5" required><br><br>

                <button type="submit">Add Course</button>
            </form>
            """);
        html.append("</div>");

        html.append("<div class='card'>");
        html.append("<h2>Your Courses</h2>");
        html.append("<table>");
        html.append("<tr><th>ID</th><th>Name</th><th>Difficulty</th></tr>");

        for (Course course : model.getDatabase().getAllCourses().values()) {
            html.append("<tr>");
            html.append("<td>").append(course.getCourseId()).append("</td>");
            html.append("<td>").append(course.getCourseName()).append("</td>");
            html.append("<td>").append(course.getCourseDifficulty()).append("/5</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</div>");

        html.append(pageEnd());

        return html.toString();
    }

    public static String renderAssessmentsPage(SmartStudyModel model) {
        StringBuilder html = new StringBuilder();

        html.append(pageStart("Assessments"));
        html.append(header("Assessments", true));

        html.append("<div class='card'>");
        html.append("<h2>Add Assessment</h2>");

        if (model.getDatabase().getAllCourses().isEmpty()) {
            html.append("<p>You need to add a course before adding assessments.</p>");
            html.append("<a href='/courses'>Add a course</a>");
        } else {
            html.append("""
                <form action="/assessments" method="post">
                    <label>Course</label><br>
                    <select name="courseId" required>
                """);

            for (Course course : model.getDatabase().getAllCourses().values()) {
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
        }

        html.append("</div>");

        html.append("<div class='card'>");
        html.append("<h2>Your Assessments</h2>");

        if (model.getDatabase().getAllAssessments().isEmpty()) {
            html.append("<p>No assessments yet.</p>");
        } else {
            html.append("<table>");
            html.append("<tr><th>ID</th><th>Name</th><th>Course</th><th>Type</th><th>Due Date</th><th>Estimated Time</th><th>Weight</th><th>Difficulty</th></tr>");

            for (Assessment assessment : model.getDatabase().getAllAssessments().values()) {
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
        }

        html.append("</div>");
        html.append(pageEnd());

        return html.toString();
    }

    public static String renderAvailabilityPage(SmartStudyModel model) {
        Profile currentProfile = model.getCurrentProfile();

        return pageStart("Availability")
                + header("Availability", true)
                + """
            <div class="card">
                <h2>Weekly Availability</h2>
                <p>Enter how many minutes you can study each day.</p>

                <form action="/availability" method="post">
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

                <p><strong>Current Availability:</strong> """
                + currentProfile.getMinutesAvailablePerDay()
                + """
                </p>
            </div>
            """
                + pageEnd();
    }

    /**
     * Builds the main dashboard page.
     * This method is the "View" part of MVC.
     * It only focuses on what the user sees in the browser.
     */
    public static String renderDashboard(SmartStudyModel model) {
        Profile currentProfile = model.getCurrentProfile();
        WeekPlan plan = model.generateCurrentWeekPlan();

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
                        .error-card {
                            background: white;
                            padding: 24px;
                            border-radius: 16px;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                            max-width: 650px;
                            margin: 60px auto;
                        }
                        a {
                            color: #1f2937;
                            font-weight: bold;
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

        renderProfileCard(html, currentProfile);
        renderCourseStatCard(html, model);
        renderAssessmentStatCard(html, model);

        html.append("</div>");

        renderAvailabilityCard(html, currentProfile);
        renderCoursesCard(html, model);
        renderAssessmentsCard(html, model);
        renderWeekPlanCard(html, plan);

        html.append("""
                    </div>
                </body>
                </html>
                """);

        return html.toString();
    }

    private static void renderProfileCard(StringBuilder html, Profile currentProfile) {
        html.append("<div class='card'>");
        html.append("<h2>Profile</h2>");
        html.append("<p><strong>Name:</strong> ").append(currentProfile.getName()).append("</p>");
        html.append("<p><strong>Profile ID:</strong> ").append(currentProfile.getProfileId()).append("</p>");
        html.append("<p><strong>Max Daily Study:</strong> ")
                .append(currentProfile.getMaxDailyStudyMinutes())
                .append(" minutes</p>");

        html.append("""
                <form action="/profile" method="post" style="margin-top: 15px;">
                    <label>Name</label><br>
                    <input type="text" name="name" required><br><br>

                    <label>PIN</label><br>
                    <input type="text" name="pin" minlength="4" maxlength="4" pattern="\\d{4}" required><br><br>

                    <label>Max Daily Study Minutes</label><br>
                    <input type="number" name="maxDailyStudyMinutes" min="0" required><br><br>

                    <button type="submit">Update Profile</button>
                </form>
                """);

        html.append("</div>");
    }

    private static void renderCourseStatCard(StringBuilder html, SmartStudyModel model) {
        html.append("<div class='card'>");
        html.append("<h2>Courses</h2>");
        html.append("<div class='stat'>")
                .append(model.getCourseCount())
                .append("</div>");
        html.append("</div>");
    }

    private static void renderAssessmentStatCard(StringBuilder html, SmartStudyModel model) {
        html.append("<div class='card'>");
        html.append("<h2>Assessments</h2>");
        html.append("<div class='stat'>")
                .append(model.getAssessmentCount())
                .append("</div>");
        html.append("</div>");
    }

    private static void renderAvailabilityCard(StringBuilder html, Profile currentProfile) {
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
    }

    private static void renderCoursesCard(StringBuilder html, SmartStudyModel model) {
        html.append("<div class='card'>");
        html.append("<h2>Courses</h2>");

        html.append("""
                <form action="/courses" method="post" style="margin-bottom: 20px;">
                    <label>Course Name</label><br>
                    <input type="text" name="courseName" required><br><br>

                    <label>Difficulty (1-5)</label><br>
                    <input type="number" name="courseDifficulty" min="1" max="5" required><br><br>

                    <button type="submit">Add Course</button>
                </form>
                """);

        html.append("<table>");
        html.append("<tr><th>ID</th><th>Name</th><th>Difficulty</th></tr>");

        for (Course course : model.getCourses()) {
            html.append("<tr>");
            html.append("<td>").append(course.getCourseId()).append("</td>");
            html.append("<td>").append(course.getCourseName()).append("</td>");
            html.append("<td>").append(course.getCourseDifficulty()).append("/5</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</div>");
        html.append("<br>");
    }

    private static void renderAssessmentsCard(StringBuilder html, SmartStudyModel model) {
        html.append("<div class='card'>");
        html.append("<h2>Assessments</h2>");

        html.append("""
                <form action="/assessments" method="post" style="margin-bottom: 20px;">
                    <label>Course</label><br>
                    <select name="courseId" required>
                """);

        for (Course course : model.getCourses()) {
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

        for (Assessment assessment : model.getAssessments()) {
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
    }

    private static void renderWeekPlanCard(StringBuilder html, WeekPlan plan) {
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
    }

    /**
     * Builds a simple error page.
     * The controller uses this when a form submission fails.
     */
    public static String renderError(String title, String message) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Error</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: #f4f6fb;
                            color: #222;
                        }
                        .error-card {
                            background: white;
                            padding: 24px;
                            border-radius: 16px;
                            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                            max-width: 650px;
                            margin: 60px auto;
                        }
                        a {
                            color: #1f2937;
                            font-weight: bold;
                        }
                    </style>
                </head>
                <body>
                    <div class="error-card">
                        <h1>""" + title + """
                        </h1>
                        <p>""" + message + """
                        </p>
                        <a href="/">Back to Dashboard</a>
                    </div>
                </body>
                </html>
                """;
    }




    private static String pageEnd() {
        return """
            </div>
            </body>
            </html>
            """;
    }

    private static String header(String title, boolean showNav) {
        String nav = "";

        if (showNav) {
            nav = """
                <nav>
                    <a href="/dashboard">Dashboard</a>
                    <a href="/courses">Courses</a>
                    <a href="/assessments">Assessments</a>
                    <a href="/availability">Availability</a>
                    <a href="/logout">Logout</a>
                </nav>
                """;
        }

        return """
            <div class="header">
                <h1>""" + title + """
                </h1>
                <p>Plan your courses, deadlines, and weekly study time.</p>
                """ + nav + """
            </div>
            """;
    }

    private static String pageStart(String title) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>""" + title + """
                </title>
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
                    .card {
                        background: white;
                        padding: 20px;
                        border-radius: 16px;
                        box-shadow: 0 4px 12px rgba(0,0,0,0.08);
                        margin-bottom: 20px;
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
                    nav a {
                        margin-right: 14px;
                        color: white;
                        font-weight: bold;
                    }
                    a {
                        color: #1f2937;
                        font-weight: bold;
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
            """;
    }
}