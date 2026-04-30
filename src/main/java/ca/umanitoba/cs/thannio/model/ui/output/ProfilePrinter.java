package ca.umanitoba.cs.thannio.model.ui.output;

import ca.umanitoba.cs.thannio.model.Profile;

public class ProfilePrinter {

    public ProfilePrinter() {}

    public static void showProfile(Profile profile){
        System.out.println("\nProfile: " + "\nStudent Name: " + profile.getName() + ".\n" + "Max Daily Study Minutes: " + profile.getMaxDailyStudyMinutes() + ".");
        System.out.println("\nAvailability: " + "\n" + profile.getMinutesAvailablePerDay());
    }
}
