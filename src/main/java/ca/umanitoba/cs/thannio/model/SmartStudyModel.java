package ca.umanitoba.cs.thannio.model;

import ca.umanitoba.cs.thannio.model.exceptions.*;
import ca.umanitoba.cs.thannio.model.logic.PlanningService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.sql.ResultSet;
import java.util.Collection;

public class SmartStudyModel {
    private final AppDatabase database;
    private Profile currentProfile;

    private static final String DATABASE_URL = "jdbc:sqlite:smart-study.db";

    public SmartStudyModel() {
        this.database = new AppDatabase();

        // Create SQLite tables if they do not already exist.
        initializeDatabase();

        // No user should be logged in when the app first starts.
        currentProfile = null;
    }

    public Collection<Course> getCourses() {
        return database.getAllCourses().values();
    }

    public Collection<Assessment> getAssessments() {
        return database.getAllAssessments().values();
    }

    public int getCourseCount() {
        return database.getAllCourses().size();
    }

    public int getAssessmentCount() {
        return database.getAllAssessments().size();
    }

    /**
     * Creates the database tables if they do not already exist.
     * It reads and runs the SQL from create-tables.sql.
     */
    private void initializeDatabase() {
        String sqlScript = readSqlFile("create-tables.sql");
        executeSqlScript(sqlScript);
    }

    /**
     * Reads a SQL file from src/main/resources.
     */
    private String readSqlFile(String fileName) {
        InputStream inputStream = SmartStudyModel.class
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new RuntimeException("Could not find SQL file: " + fileName);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            return reader.lines().collect(Collectors.joining("\n"));

        } catch (Exception e) {
            throw new RuntimeException("Could not read SQL file: " + fileName, e);
        }
    }

    /**
     * Runs the SQL statements from the SQL file.
     */
    private void executeSqlScript(String sqlScript) {
        String[] statements = sqlScript.split(";");

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            for (String sql : statements) {
                String cleanedSql = sql.trim();

                if (!cleanedSql.isEmpty()) {
                    statement.execute(cleanedSql);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database setup failed", e);
        }
    }

    /**
     * Opens a connection to the SQLite database.
     * Also turns on foreign key support for this connection.
     */
    private Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);

        try (Statement statement = connection.createStatement()) {
            statement.execute("pragma foreign_keys = on");
        }

        return connection;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public WeekPlan generateCurrentWeekPlan() {
        if (currentProfile == null) {
            throw new RuntimeException("No profile is currently logged in.");
        }

        PlanningService planningService = new PlanningService();

        return planningService.generateWeekPlan(
                currentProfile,
                new ArrayList<>(getAssessments()),
                LocalDate.now()
        );
    }

    //--------------------------------------------------------PROFILE SAVE & LOAD--------------------------------------------------------------------------------------------------------------------------------------------------

    public int registerProfile(String name, String pin, int maxDailyStudyMinutes) throws Exception {
        int generatedProfileId = saveNewProfile(name, pin, maxDailyStudyMinutes);

        Profile newProfile = new Profile.ProfileBuilder()
                .profileId(generatedProfileId)
                .name(name)
                .pin(pin)
                .maxDailyStudyMinutes(maxDailyStudyMinutes)
                .build();

        currentProfile = newProfile;

        try {
            database.addProfile(newProfile);
        } catch (DuplicateProfileException ignored) {
            // Profile already exists in memory.
        }

        setDefaultAvailability();
        saveAvailability();

        return generatedProfileId;
    }

    /**
     * Saves a profile to SQLite.
     * If the profile already exists, it updates the existing row.
     */
    private void saveProfile(Profile profile) {
        String sql = """
            insert into profiles (
                profile_id,
                name,
                pin,
                max_daily_study_minutes
            )
            values (?, ?, ?, ?)
            on conflict(profile_id) do update set
                name = excluded.name,
                pin = excluded.pin,
                max_daily_study_minutes = excluded.max_daily_study_minutes
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, profile.getProfileId());
            statement.setString(2, profile.getName());
            statement.setString(3, profile.getPin());
            statement.setInt(4, profile.getMaxDailyStudyMinutes());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Could not save profile", e);
        }
    }

    private int saveNewProfile(String name, String pin, int maxDailyStudyMinutes) {
        String sql = """
            insert into profiles (
                name,
                pin,
                max_daily_study_minutes
            )
            values (?, ?, ?)
            returning profile_id
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, pin);
            statement.setInt(3, maxDailyStudyMinutes);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("profile_id");
                }
            }

            throw new RuntimeException("Could not get generated profile ID");

        } catch (SQLException e) {
            throw new RuntimeException("Could not save new profile", e);
        }
    }

    private void setDefaultAvailability() throws Exception {
        currentProfile.setAvailableMinutes(DayOfWeek.MONDAY, 0);
        currentProfile.setAvailableMinutes(DayOfWeek.TUESDAY, 0);
        currentProfile.setAvailableMinutes(DayOfWeek.WEDNESDAY, 0);
        currentProfile.setAvailableMinutes(DayOfWeek.THURSDAY, 0);
        currentProfile.setAvailableMinutes(DayOfWeek.FRIDAY, 0);
        currentProfile.setAvailableMinutes(DayOfWeek.SATURDAY, 0);
        currentProfile.setAvailableMinutes(DayOfWeek.SUNDAY, 0);
    }

    public void login(int profileId, String pin) throws Exception {
        Profile profile = loadProfileById(profileId);

        if (profile == null) {
            throw new RuntimeException("Profile not found.");
        }

        if (!profile.getPin().equals(pin)) {
            throw new RuntimeException("Incorrect PIN.");
        }

        currentProfile = profile;

        try {
            database.addProfile(currentProfile);
        } catch (DuplicateProfileException ignored) {
            // Profile already exists in memory.
        }

        database.clearCoursesAndAssessments();

        loadAvailabilityForCurrentProfile();
        loadCoursesForCurrentProfile();
        loadAssessmentsForCurrentProfile();
    }

    private Profile loadProfileById(int profileId) {
        String sql = """
            select profile_id, name, pin, max_daily_study_minutes
            from profiles
            where profile_id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, profileId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Profile.ProfileBuilder()
                            .profileId(resultSet.getInt("profile_id"))
                            .name(resultSet.getString("name"))
                            .pin(resultSet.getString("pin"))
                            .maxDailyStudyMinutes(resultSet.getInt("max_daily_study_minutes"))
                            .build();
                }
            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException("Could not load profile", e);
        }
    }

    public void updateProfile(String name, String pin, int maxDailyStudyMinutes) throws Exception {
        int profileId = currentProfile.getProfileId();

        Profile newProfile = new Profile.ProfileBuilder()
                .profileId(profileId)
                .name(name)
                .pin(pin)
                .maxDailyStudyMinutes(maxDailyStudyMinutes)
                .build();

        currentProfile = newProfile;

        try {
            database.addProfile(newProfile);
        } catch (DuplicateProfileException ignored) {
            // If the profile already exists in memory, currentProfile still updates.
        }
        // Save the profile into SQLite.
        saveProfile(newProfile);
    }

    public boolean isLoggedIn() {
        return currentProfile != null;
    }

    public void logout() {
        currentProfile = null;
        database.clearCoursesAndAssessments();
    }

    //--------------------------------------------------------COURSE SAVE & LOAD--------------------------------------------------------------------------------------------------------------------------------------------------

    private int saveCourse(String courseName, int courseDifficulty) {
        String sql = """
            insert into courses (
                profile_id,
                course_name,
                course_difficulty
            )
            values (?, ?, ?)
            returning course_id
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, currentProfile.getProfileId());
            statement.setString(2, courseName);
            statement.setInt(3, courseDifficulty);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("course_id");
                }
            }

            throw new RuntimeException("Could not get generated course ID");

        } catch (SQLException e) {
            throw new RuntimeException("Could not save course", e);
        }
    }

    private void loadCoursesForCurrentProfile() {
        String sql = """
            select course_id, course_name, course_difficulty
            from courses
            where profile_id = ?
            order by course_id
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, currentProfile.getProfileId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = new Course.CourseBuilder()
                            .courseId(resultSet.getInt("course_id"))
                            .courseName(resultSet.getString("course_name"))
                            .courseDifficulty(resultSet.getInt("course_difficulty"))
                            .build();

                    try {
                        database.addCourse(course);
                    } catch (DuplicateCourseException ignored) {
                        // Course already exists in memory.
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not load courses", e);
        }
    }

    public void addCourse(String courseName, int courseDifficulty) throws Exception {
        int courseId = saveCourse(courseName, courseDifficulty);

        Course course = new Course.CourseBuilder()
                .courseId(courseId)
                .courseName(courseName)
                .courseDifficulty(courseDifficulty)
                .build();

        database.addCourse(course);
    }

    public void deleteCourse(int courseId) {
        deleteCourseFromDatabase(courseId);

        database.clearCoursesAndAssessments();

        loadCoursesForCurrentProfile();
        loadAssessmentsForCurrentProfile();
    }

    private void deleteCourseFromDatabase(int courseId) {
        String sql = """
            delete from courses
            where course_id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Could not delete course", e);
        }
    }

    //--------------------------------------------------------ASSESSMENT SAVE & LOAD--------------------------------------------------------------------------------------------------------------------------------------------------


    private int saveAssessment(
            int courseId,
            String assessmentName,
            AssessmentType assessmentType,
            LocalDateTime dueDate,
            int estimatedMinutes,
            int weight,
            int difficulty
    ) {
        String sql = """
            insert into assessments (
                course_id,
                name,
                type,
                due_date,
                estimated_minutes,
                weight,
                difficulty
            )
            values (?, ?, ?, ?, ?, ?, ?)
            returning assessment_id
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, courseId);
            statement.setString(2, assessmentName);
            statement.setString(3, assessmentType.name());
            statement.setString(4, dueDate.toString());
            statement.setInt(5, estimatedMinutes);
            statement.setInt(6, weight);
            statement.setInt(7, difficulty);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("assessment_id");
                }
            }

            throw new RuntimeException("Could not get generated assessment ID");

        } catch (SQLException e) {
            throw new RuntimeException("Could not save assessment", e);
        }
    }

    private void loadAssessmentsForCurrentProfile() {
        String sql = """
            select
                a.assessment_id,
                a.course_id,
                a.name,
                a.type,
                a.due_date,
                a.estimated_minutes,
                a.weight,
                a.difficulty
            from assessments a
            join courses c on a.course_id = c.course_id
            where c.profile_id = ?
            order by a.assessment_id
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, currentProfile.getProfileId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = database.getCourse(resultSet.getInt("course_id"));

                    Assessment assessment = new Assessment.AssessmentBuilder()
                            .assessmentId(resultSet.getInt("assessment_id"))
                            .course(course)
                            .name(resultSet.getString("name"))
                            .type(AssessmentType.valueOf(resultSet.getString("type")))
                            .dueDate(LocalDateTime.parse(resultSet.getString("due_date")))
                            .estimatedMinutes(resultSet.getInt("estimated_minutes"))
                            .weight(resultSet.getInt("weight"))
                            .difficulty(resultSet.getInt("difficulty"))
                            .build();

                    try {
                        database.addAssessment(assessment);
                    } catch (DuplicateAssessmentException ignored) {
                        // Assessment already exists in memory.
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not load assessments", e);
        }
    }

    public void addAssessment(
            int courseId,
            String assessmentName,
            AssessmentType assessmentType,
            LocalDateTime dueDate,
            int estimatedMinutes,
            int weight,
            int difficulty
    ) throws Exception {
        Course course = database.getCourse(courseId);

        int assessmentId = saveAssessment(
                courseId,
                assessmentName,
                assessmentType,
                dueDate,
                estimatedMinutes,
                weight,
                difficulty
        );

        Assessment assessment = new Assessment.AssessmentBuilder()
                .assessmentId(assessmentId)
                .course(course)
                .name(assessmentName)
                .type(assessmentType)
                .dueDate(dueDate)
                .estimatedMinutes(estimatedMinutes)
                .weight(weight)
                .difficulty(difficulty)
                .build();

        database.addAssessment(assessment);
    }

    public void deleteAssessment(int assessmentId) {
        deleteAssessmentFromDatabase(assessmentId);

        try {
            database.removeAssessment(assessmentId);
        } catch (AssessmentNotFoundException ignored) {
            // If it is already gone from memory, that is okay.
        }
    }

    private void deleteAssessmentFromDatabase(int assessmentId) {
        String sql = """
            delete from assessments
            where assessment_id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, assessmentId);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Could not delete assessment", e);
        }
    }

    //--------------------------------------------------------AVAILABILITY SAVE & LOAD--------------------------------------------------------------------------------------------------------------------------------------------------

    private void saveAvailability() {
        String sql = """
            insert into profile_availability (
                profile_id,
                day_of_week,
                minutes_available
            )
            values (?, ?, ?)
            on conflict(profile_id, day_of_week) do update set
                minutes_available = excluded.minutes_available
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (var entry : currentProfile.getMinutesAvailablePerDay().entrySet()) {
                statement.setInt(1, currentProfile.getProfileId());
                statement.setString(2, entry.getKey().name());
                statement.setInt(3, entry.getValue());
                statement.addBatch();
            }

            statement.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException("Could not save availability", e);
        }
    }

    private void loadAvailabilityForCurrentProfile() {
        String sql = """
            select day_of_week, minutes_available
            from profile_availability
            where profile_id = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, currentProfile.getProfileId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    DayOfWeek day = DayOfWeek.valueOf(resultSet.getString("day_of_week"));
                    int minutes = resultSet.getInt("minutes_available");

                    currentProfile.setAvailableMinutes(day, minutes);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not load availability", e);
        }
    }

    public void updateAvailability(
            int monday,
            int tuesday,
            int wednesday,
            int thursday,
            int friday,
            int saturday,
            int sunday
    ) throws Exception {
        currentProfile.setAvailableMinutes(DayOfWeek.MONDAY, monday);
        currentProfile.setAvailableMinutes(DayOfWeek.TUESDAY, tuesday);
        currentProfile.setAvailableMinutes(DayOfWeek.WEDNESDAY, wednesday);
        currentProfile.setAvailableMinutes(DayOfWeek.THURSDAY, thursday);
        currentProfile.setAvailableMinutes(DayOfWeek.FRIDAY, friday);
        currentProfile.setAvailableMinutes(DayOfWeek.SATURDAY, saturday);
        currentProfile.setAvailableMinutes(DayOfWeek.SUNDAY, sunday);
        saveAvailability();
    }
}