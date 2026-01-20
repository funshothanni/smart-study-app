package ca.umanitoba.cs.thannio.logic;

import ca.umanitoba.cs.thannio.domain.Profile;
import ca.umanitoba.cs.thannio.logic.exceptions.NoLoggedInProfileException;
import com.google.common.base.Preconditions;

public class SessionManager {
    //current account logged in
    private static Profile currentProfile;

    private SessionManager() {}

    //checks if any account is logged in
    public static boolean activeSession() {
        return currentProfile != null;
    }

    //returns the current
    public static Profile getCurrentAccount() throws NoLoggedInProfileException {
        if (currentProfile == null) {
            throw new NoLoggedInProfileException();
        }
        return currentProfile;
    }

    //sets the current user to the user passed as a parameter
    public static void startSession(Profile profile){
        Preconditions.checkNotNull(profile, "Profile can't be null");
        currentProfile = profile;
    }

    //ends the current session by setting the current user to null
    public static void endSession(){
        currentProfile = null;
    }
}
