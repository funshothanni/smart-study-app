package ca.umanitoba.cs.thannio.model.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    //this is the location of the SQLite database file
    private static final String DATABASE_URL = "jdbc:sqlite:smart-study.db";

    //private constructor to prevent making instances of this class
    private DatabaseConnection() {
    }

    /**
     * opens a connection to the SQLite database.
     *
     * @return a database connection
     * @throws SQLException if the database cannot be opened
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);

        // SQLite does not always enforce foreign keys by default,  this turns foreign key checks on for this connection.
        try (Statement statement = connection.createStatement()) {
            statement.execute("pragma foreign_keys = on");
        }

        return connection;
    }
}