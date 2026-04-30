package ca.umanitoba.cs.thannio.web;

import ca.umanitoba.cs.thannio.controller.SmartStudyController;
import ca.umanitoba.cs.thannio.model.SmartStudyModel;
import io.javalin.Javalin;

public class SmartStudyWebAppMain {
    public static void main(String[] args) {
        SmartStudyModel model = new SmartStudyModel();

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public");
        }).start(7070);

        SmartStudyController controller = new SmartStudyController(model);
        controller.registerRoutes(app);
    }
}