package ca.umanitoba.cs.thannio.view;

import ca.umanitoba.cs.thannio.model.Profile;
import ca.umanitoba.cs.thannio.model.SmartStudyModel;

public class ProfileView {
    private ProfileView() {
    }

    public static String render(SmartStudyModel model) {
        Profile profile = model.getCurrentProfile();

        return LayoutView.pageStart("Profile")
                + LayoutView.header("Profile", true)
                + """
                <div class="card">
                    <h2>Your Profile</h2>
                    <p><strong>Profile ID:</strong> """ + profile.getProfileId() + """
                    </p>
                    <p>This is the ID you use when logging in.</p>
                </div>

                <div class="card">
                    <h2>Update Profile</h2>
                    <form action="/profile" method="post">
                        <label>Name</label><br>
                        <input type="text" name="name" value=\"""" + profile.getName() + """
                        \" required><br><br>

                        <label>PIN</label><br>
                        <input type="text" name="pin" value=\"""" + profile.getPin() + """
                        \" minlength="4" maxlength="4" pattern="\\d{4}" required><br><br>

                        <label>Max Daily Study Minutes</label><br>
                        <input type="number" name="maxDailyStudyMinutes" value=\"""" + profile.getMaxDailyStudyMinutes() + """
                        \" min="0" required><br><br>

                        <button type="submit">Save Changes</button>
                    </form>
                </div>
                """
                + LayoutView.pageEnd();
    }
}