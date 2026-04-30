package ca.umanitoba.cs.thannio.controller;

import ca.umanitoba.cs.thannio.domain.AssessmentType;
import ca.umanitoba.cs.thannio.domain.SmartStudyModel;
import ca.umanitoba.cs.thannio.view.SmartStudyView;
import io.javalin.Javalin;

import java.time.LocalDateTime;

public class SmartStudyController {
    private final SmartStudyModel model;

    public SmartStudyController(SmartStudyModel model) {
        this.model = model;
    }

    /**
     * Registers all routes/endpoints for the web app.
     * The controller receives browser requests, talks to the model,
     * then sends back a view or redirects the user.
     */
    public void registerRoutes(Javalin app) {
        app.get("/", ctx -> {
            if (model.isLoggedIn()) {
                ctx.redirect("/dashboard");
            } else {
                ctx.redirect("/login");
            }
        });

        app.get("/login", ctx -> {
            ctx.html(SmartStudyView.renderLoginPage());
        });

        app.post("/login", ctx -> {
            try {
                int profileId = Integer.parseInt(ctx.formParam("profileId"));
                String pin = ctx.formParam("pin");

                model.login(profileId, pin);

                ctx.redirect("/dashboard");
            } catch (Exception e) {
                ctx.html(SmartStudyView.renderError("Login failed", e.getMessage()));
            }
        });

        app.get("/register", ctx -> {
            ctx.html(SmartStudyView.renderRegisterPage());
        });

        app.post("/register", ctx -> {
            try {
                String name = ctx.formParam("name");
                String pin = ctx.formParam("pin");
                int maxDailyStudyMinutes = Integer.parseInt(ctx.formParam("maxDailyStudyMinutes"));

                int profileId = model.registerProfile(name, pin, maxDailyStudyMinutes);

                ctx.html(SmartStudyView.renderAccountCreatedPage(profileId));
            } catch (Exception e) {
                ctx.html(SmartStudyView.renderError("Account creation failed", e.getMessage()));
            }
        });

        app.get("/dashboard", ctx -> {
            if (!model.isLoggedIn()) {
                ctx.redirect("/login");
                return;
            }

            ctx.html(SmartStudyView.renderDashboardPage(model));
        });

        app.get("/courses", ctx -> {
            if (!model.isLoggedIn()) {
                ctx.redirect("/login");
                return;
            }

            ctx.html(SmartStudyView.renderCoursesPage(model));
        });

        app.post("/courses", ctx -> {
            try {
                String courseName = ctx.formParam("courseName");
                int courseDifficulty = Integer.parseInt(ctx.formParam("courseDifficulty"));

                model.addCourse(courseName, courseDifficulty);

                ctx.redirect("/courses");
            } catch (Exception e) {
                ctx.html(SmartStudyView.renderError("Error adding course", e.getMessage()));
            }
        });

        app.get("/assessments", ctx -> {
            if (!model.isLoggedIn()) {
                ctx.redirect("/login");
                return;
            }

            ctx.html(SmartStudyView.renderAssessmentsPage(model));
        });

        app.post("/assessments", ctx -> {
            try {
                int courseId = Integer.parseInt(ctx.formParam("courseId"));
                String assessmentName = ctx.formParam("assessmentName");
                AssessmentType assessmentType = AssessmentType.valueOf(ctx.formParam("assessmentType"));
                LocalDateTime dueDate = LocalDateTime.parse(ctx.formParam("dueDate"));
                int estimatedMinutes = Integer.parseInt(ctx.formParam("estimatedMinutes"));
                int weight = Integer.parseInt(ctx.formParam("weight"));
                int difficulty = Integer.parseInt(ctx.formParam("difficulty"));

                model.addAssessment(
                        courseId,
                        assessmentName,
                        assessmentType,
                        dueDate,
                        estimatedMinutes,
                        weight,
                        difficulty
                );

                ctx.redirect("/assessments");
            } catch (Exception e) {
                ctx.html(SmartStudyView.renderError("Error adding assessment", e.getMessage()));
            }
        });

        app.get("/availability", ctx -> {
            if (!model.isLoggedIn()) {
                ctx.redirect("/login");
                return;
            }

            ctx.html(SmartStudyView.renderAvailabilityPage(model));
        });

        app.post("/availability", ctx -> {
            try {
                int monday = Integer.parseInt(ctx.formParam("monday"));
                int tuesday = Integer.parseInt(ctx.formParam("tuesday"));
                int wednesday = Integer.parseInt(ctx.formParam("wednesday"));
                int thursday = Integer.parseInt(ctx.formParam("thursday"));
                int friday = Integer.parseInt(ctx.formParam("friday"));
                int saturday = Integer.parseInt(ctx.formParam("saturday"));
                int sunday = Integer.parseInt(ctx.formParam("sunday"));

                model.updateAvailability(
                        monday,
                        tuesday,
                        wednesday,
                        thursday,
                        friday,
                        saturday,
                        sunday
                );

                ctx.redirect("/availability");
            } catch (Exception e) {
                ctx.html(SmartStudyView.renderError("Error updating availability", e.getMessage()));
            }
        });

        app.get("/logout", ctx -> {
            model.logout();
            ctx.redirect("/login");
        });
    }
}