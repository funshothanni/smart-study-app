package ca.umanitoba.cs.thannio.domain;

import ca.umanitoba.cs.thannio.domain.exceptions.*;
import com.google.common.base.Preconditions;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Profile {
    //name cannot be null
    private final String name;
    //profileId cannot be negative
    private final int profileId;
    //pin must be 4 digits
    private final String pin;
    //maxDailyStudyMinutes must always be >= 0
    private final int maxDailyStudyMinutes;
    //minutesAvailablePerDay cannot be null
    private final Map<DayOfWeek, Integer> minutesAvailablePerDay;

    private Profile(String name, int profileId, String pin, int maxDailyStudyMinutes) {
        this.name = name;
        this.profileId = profileId;
        this.pin = pin;
        this.maxDailyStudyMinutes = maxDailyStudyMinutes;
        this.minutesAvailablePerDay = new LinkedHashMap<>();
        //postcondition for constructor: this instance is in a valid state
        checkProfile();
    }

    private void checkProfile(){
        Preconditions.checkNotNull(name, "Name cannot be null");
        Preconditions.checkArgument(!name.isBlank(), "Name cannot be blank nor null");
        Preconditions.checkArgument(profileId > 0, "Profile ID cannot be negative");
        Preconditions.checkNotNull(pin, "Pin cannot be null");
        Preconditions.checkArgument(!pin.isBlank(), "Pin cannot be blank nor null");
        Preconditions.checkState(pin.matches("\\d{4}"), "Pin must be exactly 4 digits");
        Preconditions.checkArgument(maxDailyStudyMinutes >= 0, "Maximum Daily Study Minutes cannot be negative.");
        Preconditions.checkNotNull(minutesAvailablePerDay, "Minutes Available Per Day cannot be null.");
        for(Map.Entry<DayOfWeek, Integer> entry : minutesAvailablePerDay.entrySet()){
            Preconditions.checkNotNull(entry.getKey(), "Weekday cannot be null.");
            Preconditions.checkNotNull(entry.getValue(), "Minutes Available Per Day cannot be null.");
            Preconditions.checkArgument(entry.getValue() >= 0, "Minutes Available Per Day cannot be negative.");
            Preconditions.checkArgument(entry.getValue() <= maxDailyStudyMinutes, "Minutes available cannot exceed Maximum Daily Study Minutes.");
        }
    }

    public String getName() {
        return name;
    }

    public int getProfileId() {
        return profileId;
    }

    public String getPin() {
        return pin;
    }

    public  int getMaxDailyStudyMinutes() {
        return maxDailyStudyMinutes;
    }

    public Map<DayOfWeek, Integer> getMinutesAvailablePerDay() {
        return Collections.unmodifiableMap(minutesAvailablePerDay);
    }

    public void setAvailableMinutes(DayOfWeek day, int minutesAvailable) throws InvalidAvailableMinutesException {
        Preconditions.checkNotNull(day, "Day of Week cannot be null.");
        if(minutesAvailable <= 0) {
            throw new InvalidAvailableMinutesException();
        }
        if(minutesAvailable > maxDailyStudyMinutes) {
            throw new InvalidAvailableMinutesException();
        }
        minutesAvailablePerDay.put(day, minutesAvailable);
    }

    public static final class ProfileBuilder {
        private String name;
        private int profileId;
        private String pin;
        private int maxDailyStudyMinutes;

        public ProfileBuilder name(String name) throws InvalidProfileNameException {
            Preconditions.checkNotNull(name, "Profile name cannot be null");
            if(name.isBlank()) {
                throw new InvalidProfileNameException();
            }
            this.name = name;
            return this;
        }

        public ProfileBuilder profileId(int profileId) throws InvalidProfileIdException {
            if (profileId <= 0) {
                throw new InvalidProfileIdException();
            }
            this.profileId = profileId;
            return this;
        }

        public ProfileBuilder pin(String pin) throws InvalidProfilePinException {
            Preconditions.checkNotNull(pin, "Pin cannot be null");
            if (pin.isBlank()) {
                throw new InvalidProfilePinException();
            }
            if(!pin.matches("\\d{4}")){
                throw new InvalidProfilePinException();
            }
            this.pin = pin;
            return this;
        }

        public ProfileBuilder maxDailyStudyMinutes(int maxDailyStudyMinutes) throws InvalidMaxDailyStudyMinutesException {
            if (maxDailyStudyMinutes < 0) {
                throw new InvalidMaxDailyStudyMinutesException();
            }
            this.maxDailyStudyMinutes = maxDailyStudyMinutes;
            return this;
        }

        public Profile build() {
            return new Profile(name, profileId, pin, maxDailyStudyMinutes);
        }
    }
}
