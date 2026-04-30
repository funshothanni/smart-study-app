package ca.umanitoba.cs.thannio.view;

public class LayoutView {
    private LayoutView() {
    }

    public static String pageStart(String title) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>""" + title + """
                    </title>
                    <link rel="stylesheet" href="/style.css">
                </head>
                <body>
                <div class="container">
                """;
    }

    public static String header(String title, boolean showNav) {
        String nav = "";

        if (showNav) {
            nav = """
                    <nav>
                        <a href="/dashboard">Dashboard</a>
                        <a href="/courses">Courses</a>
                        <a href="/assessments">Assessments</a>
                        <a href="/availability">Availability</a>
                        <a href="/profile">Profile</a>
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

    public static String pageEnd() {
        return """
                </div>
                </body>
                </html>
                """;
    }
}