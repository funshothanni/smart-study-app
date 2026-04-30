package ca.umanitoba.cs.thannio.view;

public class LoginView {
    private LoginView() {}

    public static String render() {
        return LayoutView.pageStart("Login")
                + LayoutView.header("Smart Study App", false)
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
                + LayoutView.pageEnd();
    }
}