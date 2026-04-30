package ca.umanitoba.cs.thannio.view;

import ca.umanitoba.cs.thannio.model.Assessment;
import ca.umanitoba.cs.thannio.model.Course;
import ca.umanitoba.cs.thannio.model.SmartStudyModel;

public class AssessmentView {
    private AssessmentView() {
    }

    public static String render(SmartStudyModel model) {
        StringBuilder html = new StringBuilder();

        html.append(LayoutView.pageStart("Assessments"));
        html.append(LayoutView.header("Assessments", true));

        html.append("<div class='card'>");
        html.append("<h2>Add Assessment</h2>");

        if (model.getCourseCount() == 0) {
            html.append("<p>You need to add a course before adding assessments.</p>");
            html.append("<a href='/courses'>Add a course</a>");
        } else {
            html.append("""
                    <form action="/assessments" method="post">
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
        }

        html.append("</div>");

        html.append("<div class='card'>");
        html.append("<h2>Your Assessments</h2>");

        if (model.getAssessmentCount() == 0) {
            html.append("<p>No assessments yet.</p>");
        } else {
            html.append("<table>");
            html.append("<tr><th>ID</th><th>Name</th><th>Course</th><th>Type</th><th>Due Date</th><th>Estimated Time</th><th>Weight</th><th>Difficulty</th><th>Action</th></tr>");
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
                html.append("<td>");
                html.append("<form action='/assessments/delete' method='post' style='display:inline;'>");
                html.append("<input type='hidden' name='assessmentId' value='")
                        .append(assessment.getAssessmentId())
                        .append("'>");
                html.append("<button type='submit'>Delete</button>");
                html.append("</form>");
                html.append("</td>");
                html.append("</tr>");
            }

            html.append("</table>");
        }

        html.append("</div>");
        html.append(LayoutView.pageEnd());

        return html.toString();
    }
}