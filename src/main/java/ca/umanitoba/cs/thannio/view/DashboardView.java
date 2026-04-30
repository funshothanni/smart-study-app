package ca.umanitoba.cs.thannio.view;

import ca.umanitoba.cs.thannio.model.Profile;
import ca.umanitoba.cs.thannio.model.SmartStudyModel;
import ca.umanitoba.cs.thannio.model.StudyBlock;
import ca.umanitoba.cs.thannio.model.WeekPlan;

public class DashboardView {
    private DashboardView() {
    }

    public static String render(SmartStudyModel model) {
        Profile profile = model.getCurrentProfile();
        WeekPlan plan = model.generateCurrentWeekPlan();

        StringBuilder html = new StringBuilder();

        html.append(LayoutView.pageStart("Dashboard"));
        html.append(LayoutView.header("Dashboard", true));

        html.append("<div class='card'>");
        html.append("<h2>Welcome, ").append(profile.getName()).append("</h2>");
        html.append("<p><strong>Profile ID:</strong> ").append(profile.getProfileId()).append("</p>");
        html.append("<p><strong>Max Daily Study:</strong> ")
                .append(profile.getMaxDailyStudyMinutes())
                .append(" minutes</p>");
        html.append("<p><strong>Courses:</strong> ")
                .append(model.getCourseCount())
                .append("</p>");
        html.append("<p><strong>Assessments:</strong> ")
                .append(model.getAssessmentCount())
                .append("</p>");
        html.append("<p><a href='/profile'>Edit profile</a></p>");
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
        html.append(LayoutView.pageEnd());

        return html.toString();
    }
}