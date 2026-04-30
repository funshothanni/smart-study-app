package ca.umanitoba.cs.thannio.view;

public class RegisterView {
    private RegisterView() {}

    public static String render() {
        return LayoutView.pageStart("Create Account")
                + LayoutView.header("Create Account", false)
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
                + LayoutView.pageEnd();
    }

    public static String renderAccountCreated(int profileId) {
        return LayoutView.pageStart("Account Created")
                + LayoutView.header("Account Created", false)
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
                + LayoutView.pageEnd();
    }
}