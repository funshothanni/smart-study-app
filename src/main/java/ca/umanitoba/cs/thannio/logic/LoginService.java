package ca.umanitoba.cs.thannio.logic;

import ca.umanitoba.cs.thannio.domain.AppDatabase;
import ca.umanitoba.cs.thannio.domain.Profile;
import ca.umanitoba.cs.thannio.domain.exceptions.InvalidProfilePinException;
import ca.umanitoba.cs.thannio.domain.exceptions.ProfileNotFoundException;
import com.google.common.base.Preconditions;

public class LoginService {
    private LoginService() {
    }

    /**
     * this method performs login of an account after a correct pin is entered
     *
     * @param database       the database the account belongs to
     * @param profileID      the id of the profile logging in
     * @param pin            the pin of the user
     * @return the logged in account
     * @throws InvalidProfilePinException      an exception thrown when a wrong pin is entered
     * @throws ProfileNotFoundException an exception thrown when an account matching the account ID isn't found
     */
    public static Profile login(AppDatabase database, int profileID, String pin) throws InvalidProfilePinException, ProfileNotFoundException {
        Preconditions.checkNotNull(database, "Database can't be null");
        Preconditions.checkNotNull(profileID, "Profile ID can't be null");
        Preconditions.checkNotNull(pin, "Pin can't be null");
        Profile a = database.getProfile(profileID);
        if (!a.getPin().equals(pin)) {
            throw new InvalidProfilePinException();
        }
        return a;
    }
}
