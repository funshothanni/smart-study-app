package ca.umanitoba.cs.thannio.model.persistence;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    // private constructor because this class is only used with static methods.
    private DatabaseInitializer() {
    }

    // this reads create-tables.sql and runs the SQL commands inside it.
    public static void initialize() {
        String sql = readSqlFile("create-tables.sql");
        executeSqlScript(sql);
    }

    /**
     * reads the SQL file as text.
     *
     * @param fileName the name of the SQL file inside src/main/resources
     * @return the content of the SQL file as a string
     */
    public static String readSqlFile(String fileName) {
        InputStream inputStream = DatabaseInitializer.class
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new RuntimeException("Could not find SQL file: " + fileName);
        }

        // read the file line by line and join it into one String
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Could not read SQL file: " + fileName, e);
        }
    }

    /**
     * runs the SQL commands from the SQL file.
     *
     * @param sqlScript the full SQL file content
     */
    public static void executeSqlScript(String sqlScript) {
        // splits the file into separate SQL commands using semicolons.
        String[] statements = sqlScript.split(";");

        // open a database connection and create a statement object.
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // run each SQL command one by one.
            for (String sql : statements) {
                String cleanedSql = sql.trim();

                if (!cleanedSql.isEmpty()) {
                    statement.execute(cleanedSql);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Database setup failed", e);
        }
    }
}