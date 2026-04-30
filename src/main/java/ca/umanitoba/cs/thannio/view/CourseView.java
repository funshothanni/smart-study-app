package ca.umanitoba.cs.thannio.view;

import ca.umanitoba.cs.thannio.model.Course;
import ca.umanitoba.cs.thannio.model.SmartStudyModel;

public class CourseView {
    private CourseView() {
    }

    public static String render(SmartStudyModel model) {
        StringBuilder html = new StringBuilder();

        html.append(LayoutView.pageStart("Courses"));
        html.append(LayoutView.header("Courses", true));

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

        if (model.getCourseCount() == 0) {
            html.append("<p>No courses yet.</p>");
        } else {
            html.append("<table>");
            html.append("<tr><th>ID</th><th>Name</th><th>Difficulty</th><th>Action</th></tr>");
            for (Course course : model.getCourses()) {
                html.append("<tr>");
                html.append("<td>").append(course.getCourseId()).append("</td>");
                html.append("<td>").append(course.getCourseName()).append("</td>");
                html.append("<td>").append(course.getCourseDifficulty()).append("/5</td>");
                html.append("<td>");
                html.append("<form action='/courses/delete' method='post' style='display:inline;'>");
                html.append("<input type='hidden' name='courseId' value='")
                        .append(course.getCourseId())
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