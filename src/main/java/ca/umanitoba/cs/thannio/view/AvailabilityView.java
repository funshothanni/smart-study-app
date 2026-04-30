package ca.umanitoba.cs.thannio.view;

import ca.umanitoba.cs.thannio.model.Profile;
import ca.umanitoba.cs.thannio.model.SmartStudyModel;

public class AvailabilityView {
    private AvailabilityView() {
    }

    public static String render(SmartStudyModel model) {
        Profile currentProfile = model.getCurrentProfile();

        return LayoutView.pageStart("Availability")
                + LayoutView.header("Availability", true)
                + """
                <div class="card">
                    <h2>Weekly Availability</h2>
                    <p>Enter how many minutes you can study each day.</p>

                    <form action="/availability" method="post">
                        <label>Monday</label><br>
                        <input type="number" name="monday" min="0" required><br><br>

                        <label>Tuesday</label><br>
                        <input type="number" name="tuesday" min="0" required><br><br>

                        <label>Wednesday</label><br>
                        <input type="number" name="wednesday" min="0" required><br><br>

                        <label>Thursday</label><br>
                        <input type="number" name="thursday" min="0" required><br><br>

                        <label>Friday</label><br>
                        <input type="number" name="friday" min="0" required><br><br>

                        <label>Saturday</label><br>
                        <input type="number" name="saturday" min="0" required><br><br>

                        <label>Sunday</label><br>
                        <input type="number" name="sunday" min="0" required><br><br>

                        <button type="submit">Update Availability</button>
                    </form>

                    <p><strong>Current Availability:</strong> """
                + currentProfile.getMinutesAvailablePerDay()
                + """
                    </p>
                </div>
                """
                + LayoutView.pageEnd();
    }
}