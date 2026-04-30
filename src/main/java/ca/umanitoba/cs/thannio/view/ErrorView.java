package ca.umanitoba.cs.thannio.view;

public class ErrorView {
    private ErrorView() {}

    public static String render(String title, String message) {
        return LayoutView.pageStart("Error")
                + """
                <div class="card">
                    <h1>""" + title + """
                    </h1>
                    <p>""" + message + """
                    </p>
                    <a href="/">Back</a>
                </div>
                """
                + LayoutView.pageEnd();
    }
}